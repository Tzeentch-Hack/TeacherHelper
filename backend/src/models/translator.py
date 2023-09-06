from googletrans import Translator


class GoogleTranslator:
    def __init__(self):
        self.translator = Translator()

    def translate(self, text, source, destination):
        return self.translator.translate(text, src=source, dest=destination).text

    def detect(self, text):
        return self.translator.detect(text).lang

if __name__ == "__main__":
    uzb_text = "Qaleysan, nima ishlar?"
    trans = GoogleTranslator()
    translation = trans.translate(uzb_text, 'uz', 'en')
    print('translation:', translation)
    uzb_translation = trans.translate(translation, 'en', 'uz')
    print('uzb translation:', uzb_translation)
    print('detection of the text:', trans.detect(uzb_text))