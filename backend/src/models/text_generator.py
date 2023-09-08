import os
import time

import openai
from dotenv import load_dotenv

load_dotenv()

openai.api_key = os.getenv("OPENAI_API_KEY")


class TextGenerator:
    def __init__(self, model: str, max_tokens: int):

        if not isinstance(model, str):
            raise ValueError("Model name must be a string.")
        if not isinstance(max_tokens, int) or max_tokens <= 0:
            raise ValueError("Max tokens must be a positive integer.")

        self.model = model
        self.max_tokens = max_tokens

    def get_response(self, system: str, prompt: str, retries: int = 3) -> str:
        if not isinstance(system, str):
            raise ValueError("System prompt must be a string.")
        if not isinstance(prompt, str):
            raise ValueError("User prompt must be a string.")
        if not isinstance(retries, int) or retries < 0:
            raise ValueError("Retries must be a non-negative integer.")

        for _ in range(retries):
            try:
                print('making openai request...')
                completions = openai.ChatCompletion.create(
                    model=self.model,
                    max_tokens=self.max_tokens,
                    messages=[
                        {"role": "system", "content": system},
                        {"role": "user", "content": prompt},
                    ],
                )
                print('ended openai request...')
                return completions.choices[0].message.content
            except (openai.error.RateLimitError, openai.error.APIConnectionError, openai.error.ServiceUnavailableError):
                print(
                    "Rate limit reached or connection error occurred. Retrying..."
                )
                time.sleep(1)

        # If retries exhausted and still haven't succeeded, raise an exception
        raise Exception("Failed to get response after multiple retries.")
