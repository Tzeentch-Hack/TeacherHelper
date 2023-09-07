import pytesseract
import cv2
import numpy as np
import time


def apply_gamma_correction(image, gamma):
    lookup_table = np.array([((i / 255.0) ** gamma) * 255 for i in np.arange(0, 256)]).astype("uint8")
    gamma_corrected_image = cv2.LUT(image, lookup_table)
    return gamma_corrected_image


def add_contrast(image, alpha):
    return cv2.addWeighted(image, alpha, image, 0, 0)


def preprocess_image(image_array):
    if len(image_array.shape) == 3:
        img = cv2.cvtColor(image_array, cv2.COLOR_BGR2GRAY)
    else:
        img = image_array
    w, h = img.shape
    contr_img = add_contrast(img, 1.4)
    gamma_img = apply_gamma_correction(contr_img, 2.8)
    return gamma_img
def recognize_text_from_image(image, language='eng'):
    text = pytesseract.image_to_string(image, lang=language)
    return text.strip()

def recognize_image(image, language='uzb+rus+eng'):
    preprocessed_image = preprocess_image(image)
    recognized_text = recognize_text_from_image(preprocessed_image, language=language)
    return recognized_text

if __name__ == "__main__":

    input_image_path = "../../data/input/tarix_2.jpg"
    start = time.time()
    preprocessed_image = preprocess_image(cv2.imread(input_image_path))
    cv2.imwrite("../../data/pictures/processed.png", preprocessed_image)
    recognized_text = recognize_text_from_image(preprocessed_image, language='uzb+rus+eng')
    end = time.time()
    print(recognized_text)
    print('time taken:', end - start)