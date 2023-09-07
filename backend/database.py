import sqlalchemy.sql.sqltypes
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from sqlalchemy import Column, Integer, String, BOOLEAN


SQLALCHEMY_DATABASE_URL = "sqlite:///./databases/users.db"

engine = create_engine(
    SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False}
)

Base = sqlalchemy.orm.declarative_base()


class UserInDBSQL(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    username = Column(String)
    email = Column(String)
    full_name = Column(String)
    disabled = Column(BOOLEAN)
    hashed_password = Column(String)


class RequestItem(Base):
    __tablename__ = "request"

    id = Column(Integer, primary_key=True, index=True)
    username = Column(String)
    status = Column(String)
    request_id = Column(String)

    def to_dict(self):
        return {
            'username': self.username,
            'status': self.status,
            'request_id': self.request_id,
        }


class RequestCounter(Base):
    __tablename__ = "request_counter"

    id = Column(Integer, primary_key=True)
    request_count = Column(Integer, default=0)

    def to_dict(self):
        return {
            'request_count': self.request_count,
        }


Base.metadata.create_all(bind=engine)
SessionLocal = sessionmaker(autoflush=False, bind=engine)
db = SessionLocal()


if not db.query(RequestCounter).filter(RequestCounter.id == 1).first():
    initial_request_counter = RequestCounter(request_count=0)
    db.add(initial_request_counter)
    db.commit()

app = None
