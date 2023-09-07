from typing import Annotated, List

from fastapi import Depends, FastAPI, File, UploadFile
from fastapi.security import OAuth2PasswordBearer


import uvicorn

import authorization
import image_manager
import database
import models

app = FastAPI()
app.include_router(authorization.router)


def get_request_id():
    return (str(0)).zfill(6)


@app.get("/", tags=["Help generation"])
def root():
    return {"message": "Teacher Helper"}


@app.post("/create_request", tags=["Help generation"])
def create_request(current_user: Annotated[models.User, Depends(authorization.get_current_active_user)],
                   files: List[UploadFile] = File(...)):
    image_datas = []
    for file in files:
        try:
            contents = file.file.read()
            image_data = image_manager.FileData(filename=file.filename, content=contents)
            image_datas.append(image_data)
        except Exception:
            return {"message": "There was an error uploading the file(s)"}
        finally:
            file.file.close()
    request_id = get_request_id()
    image_manager.set_images_to_user(current_user.username, request_id, image_datas)
    return {"request_id": request_id}


@app.get("/check_request", tags=["Help generation"])
def check_request(request_id: int):
    response_body = {"content": 0}
    return response_body


@app.get("/get_all_requests", tags=["Help generation"])
def get_all_requests():
    response_body = {"content": 0}
    return response_body


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
