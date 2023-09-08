from typing import Annotated, List

from fastapi import Depends, FastAPI, File, UploadFile, Request, HTTPException
from fastapi.responses import FileResponse
from worker import infer_whole_task
import uvicorn
from celery.result import AsyncResult
from worker import celery_app
import models
import authorization
import image_manager
import requestManagement
import os

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

    image_paths = image_manager.set_images_to_user(current_user.username, request_id, image_datas)
    image_paths = [f'./backend/{path}' for path in image_paths]

    output_path = image_manager.return_slide_paths(current_user.username, request_id)
    output_path = f'./backend/{output_path}'
    print('image_paths:', image_paths)
    print('output_path:', output_path)
    task = infer_whole_task.delay(image_paths, output_path)
    requestManagement.add_processed_request(models.ProcessedRequest(status="process",
                                                                    request_id=request_id,
                                                                    username=current_user.username,
                                                                    task_id=task.id,
                                                                    images_url=[],
                                                                    pptx_url='',
                                                                    short_text='',
                                                                    lesson_estimates=[],
                                                                    teaching_recommendations=[],
                                                                    possible_questions=[]
                                                                    ))
    requestManagement.increment_request_count()
    return {"request_id": request_id}


@app.get("/all_requests", response_model=models.RequestsShortDatas, tags=["Help generation"])
def get_all_requests(current_user: Annotated[models.User, Depends(authorization.get_current_active_user)]):
    requests_short_data_list = requestManagement.get_all_requests_ids_by_username(current_user.username)
    return models.RequestsShortDatas(requests_short_data=requests_short_data_list)


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


@app.get("/get_request", response_model=models.ProcessedRequest, tags=["Help generation"])
def check_request(current_user: Annotated[models.User, Depends(authorization.get_current_active_user)],
                  request_id: str,
                  request: Request):
    pr = requestManagement.get_processed_request(request_id)
    request_path = image_manager.return_slide_paths(current_user.username, pr.request_id)
    pr.short_text = ''
    pr.teaching_recommendations = []
    pr.lesson_estimates = []
    pr.possible_questions = []
    pr.pptx_url = ''
    pr.images_url = []
    task = AsyncResult(pr.task_id, app=celery_app)

    if not task.ready():
        response = {
            'state': task.state,
            'status': 'Task is still processing'
        }
        pr.status = response['status']
    elif task.successful():
        response = {
            'state': task.state,
            'result': task.result,
            'status': 'Succeeded'
        }
        pr.status = response['status']
        pr.short_text = response['result'][0]
        pr.teaching_recommendations = response['result'][1]
        pr.lesson_estimates = response['result'][2]
        pr.possible_questions = response['result'][3]
        request_path = image_manager.return_slide_paths(current_user.username, pr.request_id)
        pr.pptx_url = os.path.join(os.path.join(f'{request.base_url}download/{request_path}'), 'temp.pptx')
        pr.images_url = [os.path.join(f'{request.base_url}download/{request_path}', path) for path in os.listdir(request_path)[1:]]
    else:
        response = {
            'state': task.state,
            'status': str(task.info)
        }
        pr.status = response['status']
    requestManagement.change_status_of_processed_request(pr)
    return pr


@app.get("/download/{path:path}",  tags=["Help generation"])
async def download_file(path: str):
    if os.path.exists(path):
        return FileResponse(path, filename=path)
    else:
        raise HTTPException(status_code=520, detail="File not found")

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
