import random
import time

import numpy as np
import torch
from diffusers import StableDiffusionPipeline, DPMSolverMultistepScheduler
import os
import datetime

os.environ['CUDA_LAUNCH_BLOCKING'] = "1"


model_id = "darkstorm2150/Protogen_v5.3_Official_Release"
device = "cuda" if torch.cuda.is_available() else "cpu"
print('device:', device)


class StableGenerator:
    def __init__(self, model_id: str):
        self.pipe = StableDiffusionPipeline.from_pretrained(model_id, torch_dtype=torch.float16)
        self.pipe.scheduler = DPMSolverMultistepScheduler.from_config(self.pipe.scheduler.config)
        def dummy(images, *args, **kwargs):
            return images, [False]

        self.pipe.safety_checker = dummy
        self.pipe = self.pipe.to(device)
        self.initialized = True


    def generate_image(self, prompt):
        times = int(time.time_ns() % (1e9 + 7))
        random.seed(times)
        np.random.seed(times)
        generator_seed = random.randint(1, 1000000000)
        generator = torch.Generator(device).manual_seed(generator_seed)
        #megative prompt
        pipes = self.pipe(prompt, num_inference_steps=25, negative_prompt="nude, naked", height=512, width=768, guidance_scale=7.5, generator=generator)
        image = pipes.images[0]
        date_string = datetime.datetime.now().strftime('%Y-%m-%dT%H:%M+%S')
        return image, date_string

if __name__ == "__main__":
    sd_gen = StableGenerator(model_id)
    prompt = "modelshoot style, (extremely detailed 8k wallpaper),a full shot picture of a (castle on the hill), Intricate, High Detail, dramatic"
    image, date_string = sd_gen.generate_image(prompt)
    image.save('../../data/pictures/temp/' + date_string + '.png')
