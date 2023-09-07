import os
from dataclasses import dataclass

image_path = "./Images"


@dataclass
class FileData:
    filename: str
    content: bytes


def set_images_to_user(username, request_id, image_datas):
    if not os.path.exists(image_path):
        os.makedirs(image_path)
    user_path = os.path.join(image_path, username)
    if not os.path.exists(user_path):
        os.makedirs(user_path)
    request_path = os.path.join(user_path, request_id)
    if not os.path.exists(request_path):
        os.makedirs(request_path)
    try:
        for image_data in image_datas:
            with open(os.path.join(request_path, image_data.filename), 'wb') as f:
                f.write(image_data.content)
    except Exception("I\\O error") as e:
        raise e


