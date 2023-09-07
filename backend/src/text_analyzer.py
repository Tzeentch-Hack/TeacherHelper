from backend.src.models.text_generator import TextGenerator
from backend.src.models.translator import GoogleTranslator
from concurrent.futures import ThreadPoolExecutor, as_completed
import time
import re

KEY_PRESENTATION_PARTS = 'presentation_parts'
KEY_RECOMMENDATIONS = 'teaching_recommendations'
KEY_LESSON_STRUCTURE_AND_ESTIMATES = 'lesson_structure_and_estimates'
KEY_POSSIBLE_QUESTIONS = 'possible_questions'

class TextAnalyzer:
    def __init__(self, model, max_tokens):
        self.model = model
        self.max_tokens = max_tokens
        self.prompt = "Text to analyze:\n"
        self.text_generator = TextGenerator(self.model, self.max_tokens)
        self.translator = GoogleTranslator()

    def _translate_to_en(self, input_text):
        source_language = self.translator.detect(input_text)
        return (input_text if source_language == 'en' else
                self.translator.translate(input_text, source_language, 'en'), source_language)

    def _gpt_query(self, system_instruction, input_text):
        translated_text, detected_language = self._translate_to_en(input_text)
        prompt = self.prompt + translated_text
        gpt_response = self.text_generator.get_response(system=system_instruction, prompt=prompt)


        if detected_language != 'en':
            return self.translator.translate(gpt_response, 'en', detected_language), gpt_response
        return gpt_response, gpt_response

    def _parse_presentation_parts(self, text):
        topics = re.split(r'\d+\.', text)[1:]
        parsed_output = []

        for topic in topics:
            topic_name, *options = [x.strip() for x in topic.split("\n") if x.strip()]
            parsed_output.append({
                'topic_name': topic_name,
                'options': options
            })
        return parsed_output

    def _parse_list_output(self, text):
        items = re.split(r'\d+\.', text)[1:]
        return [x.strip() for x in items if x.strip()]

    def get_presentation_parts(self, input_text):
        system_instruction = (
            "From the text, extract 4-5 key topics and present them sequentially. For each topic, provide a brief structured description consisting of 3-4 points. Format as follows:\n\n1. [Topic Name]: \n- First option\n- Second option\n- Third option\n2. [Next Topic Name]: \n- First option\n- Second option\n- Third option\n3. ...\nEnsure each entry is numbered up to 4 or 5, and that the topic is clearly separated from its point-form description. Make each option ONE SHORT, CONCISE sentence with FEW words")
        gpt_response_source, gpt_response_en = self._gpt_query(system_instruction, input_text)
        return self._parse_presentation_parts(gpt_response_source), self._parse_presentation_parts(gpt_response_en), gpt_response_source

    def get_picture_descriptions(self, presentation_parts):
        topics = [part['topic_name'][:-1] for part in presentation_parts]
        topics_str = ', '.join(topics)

        system_instruction = (
            "For each topic listed, visualize a minimal scene or object that captures its essence. "
            "Write each description in a few key words. Avoid full sentences.")

        gpt_response_source, gpt_response_en = self._gpt_query(system_instruction, "Topics to describe:" + topics_str)

        # Modified regex pattern to capture numbered descriptions better
        pattern = r'(\d+\.\s*[^0-9]+)(?=\d+\.|$)'
        descriptions = [match.group(1).split('.')[1].strip().split(": ")[1] for match in
                        re.finditer(pattern, gpt_response_en)]

        return descriptions


    def get_teaching_recommendations(self, input_text):
        system_instruction = ("You are an expert to provide recommendations on how to teach a lesson based on the text provided. List them as:\n1. ...\n2. ...\n3. ...")
        gpt_response_source, gpt_response_en = self._gpt_query(system_instruction, input_text)
        return self._parse_list_output(gpt_response_source)

    def get_lesson_structure_and_estimates(self, input_text):
        system_instruction = ("You are an expert who can create a perfect structure for a lesson and also estimate the duration for each part based on the text provided. List them as:\n1. ...\n2. ...\n3. ...")
        gpt_response_source, gpt_response_en = self._gpt_query(system_instruction, input_text)
        return self._parse_list_output(gpt_response_source)

    def get_possible_questions(self, input_text):
        system_instruction = ("You are an expert at making possible questions students might have from the text provided. List them as:\n1. Question ...\n2. Question ...\n3. Question ...")
        gpt_response_source, gpt_response_en = self._gpt_query(system_instruction, input_text)
        return self._parse_list_output(gpt_response_source)

    def analyze_presentation(self, input_text):
        with ThreadPoolExecutor(max_workers=1) as executor:
            future_presentation = executor.submit(self.get_presentation_parts, input_text)
            presentation_result = future_presentation.result()
        return presentation_result

    def analyze_text(self, input_text):
        with ThreadPoolExecutor(
                max_workers=3) as executor:
            future_to_function = {
                executor.submit(self.get_teaching_recommendations, input_text): KEY_RECOMMENDATIONS,
                executor.submit(self.get_lesson_structure_and_estimates,
                                input_text): KEY_LESSON_STRUCTURE_AND_ESTIMATES,
                executor.submit(self.get_possible_questions, input_text): KEY_POSSIBLE_QUESTIONS
            }

            results = {}
            for future in as_completed(future_to_function):
                function_name = future_to_function[future]
                results[function_name] = future.result()

        return results


if __name__ == "__main__":
    start = time.time()
    analyzer = TextAnalyzer('gpt-3.5-turbo', max_tokens=1500)
    uzb_text = """Vatan – bu biz tug‘ilib o‘sgan zamin, kindik qoni-
                miz to‘kilgan tuproq, bobolarimiz izlari qolgan,
                ajdodlarimiz merosi avlodlarimizga qoladigan
                yurtdir.
                Dunyoda Vatanga muhabbatdan qudratliroq biron-bir tuyg‘u va uning
                sog‘inchidan kuchliroq his yo‘q. Vatan millatni yuksaltirguvchi milliy
                g‘ururdek jozibador sehrli bir tuyg‘uni uyg‘otadi. Xalq qanchalik or-no-
                musli, g‘ururli bo‘lsa, undagi Vatan hissi va milliy ong ham shunchalik
                yuqori bo‘ladi. Ona yurt – bir umrlik makon, diyor tabiati, el urf-odatlari,
                an’analari, ertak va afsonalaridir. Vatan hamisha navqiron va suyuklidir.
                Vatan millatning ma’naviy-madaniy zaminini yaratib beradi va har bir
                avlod bobolarining yurtga bo‘lgan muhabbatlarini o‘zida tashigan holda
                so‘nmas Vatan mehri bilan bu dunyoga qadam qo‘yadi.
                Har qaysi xalq milliy qadriyatlarini o‘z maqsad-muddaolari, shu
                bilan birga, umumbashariy taraqqiyot yutuqlari asosida rivojlantirib,
                ma’naviy dunyosini yuksaltirib borishga intilar ekan, bu borada tarixiy
                xotira masalasi alohida ahamiyat kasb etadi. Ya’ni tarixiy xotira tuyg‘usi
                to‘laqonli ravishda tiklangan, xalq bosib o‘tgan yo‘l o‘zining barcha mu-
                vaffaqiyat va zafarlari, yo‘qotish va qurbonlari, quvonch va iztiroblari bi-
                lan xolis va haqqoniy o‘rganilgan taqdirdagina chinakam tarix bo‘ladi."""
    start = time.time()
    analyzed_presentation_source, analyzed_presentation_eng, source_text = analyzer.analyze_presentation(uzb_text)
    print('analyzed_presentation source:', analyzed_presentation_source)
    print('analyzed_presentation eng:', analyzed_presentation_eng)
    picture_descriptions = analyzer.get_picture_descriptions(analyzed_presentation_eng)
    print('Picture Descriptions:', picture_descriptions)
    end = time.time()
    print('time taken for picture descriptions:', end - start)


    start = time.time()
    analyzed_json = analyzer.analyze_text(uzb_text)
    print('analyzed_text:', analyzed_json)
    end = time.time()
    print("time taken for analyzing 3 options:", end-start)