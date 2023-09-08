from backend.src.models import picture_generator, text_generator, text_recognition
from backend.src.models.text_recognition import recognize_image
from backend.src import presentation_builder, text_analyzer
import cv2
import os


class Pipeline:
    def __init__(self):
        self.pic_gen_model_id = "darkstorm2150/Protogen_v5.3_Official_Release"
        self.pic_gen = picture_generator.StableGenerator(self.pic_gen_model_id)
        self.text_analyzer_model = 'gpt-3.5-turbo'
        self.text_analyzer_max_tokens = 1500
        self.text_analyzer = text_analyzer.TextAnalyzer(self.text_analyzer_model, self.text_analyzer_max_tokens)

    def recognize_images(self, image_paths):
        print('image_paths:', image_paths)
        image_files = []
        for image_path in image_paths:
            image_files.append(cv2.imread(image_path))
        total_text = ""
        for image_file in image_files:
            total_text += recognize_image(image_file) + '\n'
        return total_text

    def infer_first_step(self, text):
        analyzed_presentation_source, analyzed_presentation_eng, source_text = self.text_analyzer.analyze_presentation(
            text)
        picture_descriptions = self.text_analyzer.get_picture_descriptions(analyzed_presentation_eng)
        print('descriptions:', picture_descriptions)
        return analyzed_presentation_source, picture_descriptions, source_text

    def get_one_slide(self, title, phrases, img_path, output_path):
        presentation_builder.create_pptx_and_convert_to_jpg(title, phrases, img_path, output_path)

    def get_presentation(self, slides_data, output_path):
        presentation_builder.create_pptx_multiple_slides(slides_data, output_path=output_path)

    def infer_second_step(self, text):
        analyzed_json = self.text_analyzer.analyze_text(text)
        return analyzed_json

    def infer_whole(self, input_paths, output_path):
        text = self.recognize_images(input_paths)
        analyzed_presentation_source, picture_descriptions, source_text = self.infer_first_step(text)
        img_paths = []
        for i, picture_description in enumerate(picture_descriptions, 1):
            img_path = os.path.join(output_path, f"{i}.jpg")
            self.pic_gen.generate_image(picture_description, output_path=img_path)
            img_paths.append(img_path)

        for d, s in zip(analyzed_presentation_source, img_paths):
            d["img_path"] = s
        for info_for_slide in analyzed_presentation_source:
            presentation_builder.create_pptx_and_convert_to_jpg(info_for_slide['topic_name'], info_for_slide['options'], info_for_slide['img_path'], info_for_slide['img_path'][:-4])
        self.get_presentation(analyzed_presentation_source, output_path=os.path.join(output_path, "temp.pptx"))
        analyzed_json = self.infer_second_step(text)
        return (source_text, analyzed_json['teaching_recommendations'], analyzed_json['lesson_structure_and_estimates'],
                analyzed_json['possible_questions'],)


if __name__ == "__main__":
    input_paths = ["../data/input/uzb.png"]
    pipeline = Pipeline()
    analyzed_json = pipeline.infer_whole(input_paths)
    print('analyzed_json', analyzed_json)
