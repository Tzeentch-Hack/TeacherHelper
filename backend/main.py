from typing import Annotated, List

from fastapi import Depends, FastAPI, File, UploadFile
from fastapi.security import OAuth2PasswordBearer


import uvicorn

import models
import database
import authorization
import image_manager
import requestManagement


app = FastAPI()
app.include_router(authorization.router)


def get_request_id():
    current_request_count = requestManagement.get_request_count()
    return (str(current_request_count)).zfill(6)


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
    requestManagement.add_processed_request(models.ProcessedRequest(status="process", request_id=request_id,
                                            username=current_user.username))
    image_manager.set_images_to_user(current_user.username, request_id, image_datas)
    # logica
    # ot Igorya
    requestManagement.increment_request_count()
    return {"request_id": request_id}


@app.get("/all_requests", response_model=models.RequestsIds, tags=["Help generation"])
def get_all_requests(current_user: Annotated[models.User, Depends(authorization.get_current_active_user)]):
    requests_list = requestManagement.get_all_requests_ids_by_username(current_user.username)
    return models.RequestsIds(request_ids=requests_list)


@app.get("/is_request_exist", tags=["Debug functions"])
def delete_request(current_user: Annotated[models.User, Depends(authorization.get_current_active_user)],
                   request_id: str):
    pr = requestManagement.exist_in_processed_requests(request_id)
    response_body = {pr}
    return response_body


@app.get("/delete_request", tags=["Debug functions"])
def delete_request(current_user: Annotated[models.User, Depends(authorization.get_current_active_user)],
                  request_id: str):
    pr = requestManagement.delete_processed_request_by_id(request_id)
    response_body = {"request_id": request_id}
    return response_body


@app.get("/get_request", tags=["Help generation"])
def check_request(current_user: Annotated[models.User, Depends(authorization.get_current_active_user)],
                  request_id: str):
    pr = requestManagement.get_processed_request(request_id)
    response_body = {"request_id": pr.request_id}
    return response_body


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
