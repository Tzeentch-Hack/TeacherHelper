from database import db
from database import RequestCounter
from database import RequestItem

import models


def increment_request_count():
    request_counter = db.query(RequestCounter).first()
    request_counter.request_count += 1
    db.commit()


def get_request_count():
    return db.query(RequestCounter).first().request_count


def get_processed_request(request_id) -> models.ProcessedRequest:
    request_item = db.query(RequestItem).filter_by(request_id=request_id).first()
    value = models.ProcessedRequest(request_id=request_item.request_id,
                                    username=request_item.username,
                                    status=request_item.status,
                                    task_id=request_item.task_id,
                                    images_url=request_item.images_url,
                                    pptx_url=request_item.pptx_url,
                                    short_text=request_item.short_text,
                                    lesson_estimates=request_item.lesson_estimates,
                                    teaching_recommendations=request_item.teaching_recommendations
                                    )
    return value


#def get_processed_requests():
#    request_items = db.query(RequestItem).all()
#    processed_requests = []
#    for request_item in request_items:
#        value = {"username": request_item.username, "status": request_item.status,
#                 "request_id": request_item.request_id}
#        processed_requests.append(value)
#    return processed_requests


#def change_status_of_processed_request(value: models.ProcessedRequest):
#    request_item = db.query(RequestItem).filter_by(request_id=value["request_id"]).first()
#    request_item.status = value["status"]
#    request_item.username = value["username"]
#    db.session.commit()


def exist_in_processed_requests(request_id):
    count = db.query(RequestItem).filter_by(request_id=request_id).count()
    if count > 0:
        return True
    return False


#def delete_processed_request_by_username(username: str):
#    requests_to_delete = db.query(RequestItem).filter_by(RequestItem.username=username).all()
#    for pr in requests_to_delete:
#        db.session.delete(pr)
#    db.session.commit()

def get_all_requests_ids_by_username(username):
    request_items = db.query(RequestItem).filter_by(username=username).all()
    request_short_data_list = []
    for request_item in request_items:
        request_short_data = models.RequestShort(request_id=request_item.request_id, status=request_item.status)
        request_short_data_list.append(request_short_data)
    return request_short_data_list


def delete_processed_request_by_id(request_id: str):
    request_item = db.query(RequestItem).filter_by(request_id=request_id).first()
    db.delete(request_item)
    db.commit()
    return request_id


def add_processed_request(value: models.ProcessedRequest):
    obj = RequestItem()
    obj.request_id = value.request_id
    obj.task_id = value.task_id
    obj.status = value.status
    obj.username = value.username
    obj.images_url = value.images_url
    obj.pptx_url = value.pptx_url
    obj.short_text = value.short_text
    obj.lesson_estimates = value.lesson_estimates
    obj.teaching_recommendations = value.teaching_recommendations
    db.add(obj)
    db.commit()
