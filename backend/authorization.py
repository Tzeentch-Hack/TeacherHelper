from typing import Annotated
from datetime import datetime, timedelta

from fastapi import Depends, FastAPI, HTTPException, status
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from fastapi import APIRouter
from jose import JWTError, jwt
from passlib.context import CryptContext
from pydantic import BaseModel

import models
import database

router = APIRouter()

SECRET_KEY = "DanilBorisAlexanderIgorDanilBorisAlexanderIgorDanilBorisAlexande"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60


oauth2_scheme = OAuth2PasswordBearer(tokenUrl="login")


class UserInDB(models.User):
    hashed_password: str


pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


def get_user(username: str):
    user_from_sql = database.db.query(database.UserInDBSQL).filter(database.UserInDBSQL.username == username).first()
    return models.UserInDB(username=user_from_sql.username, hashed_password=user_from_sql.hashed_password)


def username_exist(username: str):
    if database.db.query(database.UserInDBSQL).filter(database.UserInDBSQL.username == username).first():
        return True
    else:
        return False


def verify_password(plain_password, hashed_password):
    return pwd_context.verify(plain_password, hashed_password)


def get_password_hash(password):
    return pwd_context.hash(password)


def authenticate_user(username: str, password: str):
    user = get_user(username)
    if not user:
        return False
    if not verify_password(password, user.hashed_password):
        return False
    return user


def create_access_token(data: dict, expires_delta: timedelta | None = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=15)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt


def get_current_user(token: Annotated[str, Depends(oauth2_scheme)]):
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        if username is None:
            raise credentials_exception
        token_data = models.TokenData(username=username)
    except JWTError:
        raise credentials_exception
    user = get_user(username=token_data.username)
    if user is None:
        raise credentials_exception
    return user


def get_current_active_user(
    current_user: Annotated[models.User, Depends(get_current_user)]
):
    if current_user.disabled:
        raise HTTPException(status_code=400, detail="Inactive user")
    return current_user


@router.post("/login", response_model=models.Token, tags=["User management"])
def login_for_access_token(
    form_data: Annotated[OAuth2PasswordRequestForm, Depends()]
):
    user = authenticate_user(form_data.username, form_data.password)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": user.username}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}


@router.get("/users/me/", response_model=models.User, tags=["User management"])
def read_users_me(
    current_user: Annotated[models.User, Depends(get_current_active_user)]
):
    return current_user


@router.post("/registration", response_model=models.Token, tags=["User management"])
def register_new_user(form_data: Annotated[OAuth2PasswordRequestForm, Depends()]):
    new_username = form_data.username
    if username_exist(new_username):
        raise HTTPException(status_code=409, detail="User already exist")
    new_password_hash = get_password_hash(form_data.password)
    new_user = database.UserInDBSQL(username=new_username, hashed_password=new_password_hash)
    database.db.add(new_user)
    database.db.commit()
    return login_for_access_token(form_data)




