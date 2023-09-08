from celery import Celery
from backend.src.pipeline import Pipeline

celery_app = Celery('tasks')
celery_app.config_from_object('celery_config')

@celery_app.task()
def infer_whole_task(input_paths, output_paths):
    pipeline = Pipeline()
    return pipeline.infer_whole(input_paths, output_paths)
