import os
from dataclasses import dataclass

image_path = "./images"

output_path = "./outputs"
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
        list_dir = []
        for image_name in os.listdir(request_path):
            list_dir.append(os.path.join(request_path, image_name))
        return list_dir
    except Exception("I\\O error") as e:
        raise e


def return_slide_paths(username, request_id):
    os.makedirs(output_path, exist_ok=True)
    user_path = os.path.join(output_path, username)
    os.makedirs(user_path, exist_ok=True)
    request_path = os.path.join(user_path, request_id)
    os.makedirs(request_path, exist_ok=True)
    return request_path