from pydantic import BaseModel


class RequestShort(BaseModel):
    request_id: str
    status: str


class RequestsShortDatas(BaseModel):
    requests_short_data: list[RequestShort]


class ProcessedRequest(BaseModel):
    username: str
    request_id: str
    status: str


class Token(BaseModel):
    access_token: str
    token_type: str


class TokenData(BaseModel):
    username: str | None = None


class User(BaseModel):
    username: str
    email: str | None = None
    full_name: str | None = None
    disabled: bool | None = None


class UserInDB(User):
    hashed_password: str
