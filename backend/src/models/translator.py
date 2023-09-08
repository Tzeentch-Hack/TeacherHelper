from requests.exceptions import RequestException
import time
from google.cloud import translate_v2 as translate

class GoogleTranslator:
    def __init__(self):
        # Use the service account JSON file directly when creating the client
        self.client = translate.Client.from_service_account_json('/home/igore/PycharmProjects/TeacherHelper/instant-insight-398404-5dad985b3c19.json')

    def _safe_request(self, func, *args, **kwargs):
        MAX_RETRIES = 10
        RETRY_DELAY = 2  # seconds

        for attempt in range(MAX_RETRIES):
            try:
                print('waiting to translate in try...')
                return func(*args, **kwargs)
            except (RequestException, Exception) as e:
                print("Waiting to translate...", 'exception:', e)
                if attempt < MAX_RETRIES - 1:
                    time.sleep(RETRY_DELAY)
                    continue
                else:
                    raise e

    def translate(self, text, source, destination):
        print('translating request...')
        response = self._safe_request(self.client.translate, text, source_language=source, target_language=destination)
        return response['translatedText']

    def detect(self, text):
        print('detect language...')
        response = self._safe_request(self.client.detect_language, text)
        return response['language']



if __name__ == "__main__":
    pass
    uzb_text = "Qaleysan, nima ishlar?"
    trans = GoogleTranslator()
    translation = trans.translate(uzb_text, 'uz', 'en')
    print('translation:', translation)
    uzb_translation = trans.translate(translation, 'en', 'uz')
    print('uzb translation:', uzb_translation)
    print('detection of the text:', trans.detect(uzb_text))