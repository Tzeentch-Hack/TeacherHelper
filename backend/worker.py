# worker.py
from celery import Celery

app = Celery('tasks')
app.config_from_object('celery_config')

@app.task
def infer_whole_task(pipeline, input_paths):
    return pipeline.infer_whole(input_paths)
