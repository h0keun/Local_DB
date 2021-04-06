## DB_SQL

안드로이드 앱에서는 SQLite라고하는 관계형 데이터베이스를 사용한다. 용량이 적고 가볍게 동작하면서도 관계형 데이터베이스를 위한 SQL실행이 가능해서 모바일DB를 사용할 때, 광범위하게 사용한다.(최근들어서는 구글이 Room_DB를 권장함!)  
앱에서 하나의 파일로 만들어지고, 한번 만들어 두면 그 다음부터는 만들어진 데이터베이스를 오픈(openOrCreateDatabase)하여 사용한다. 이전에 SQLD 자격증을 준비하면서 공부한 문법들과 다르지 않아 낯설지 않다.  
```JAVA
public abstract SQLiteDatabase openOrCreateDatabase (String name, int mode,
					SQLiteDatabase.CursorFactory factory)
```
첫번째 파라미터는 데이터베이스의 이름이고, 위 메소드를 호출하면 SQLiteDatabase라는 객체가 반환되는데 이 객체를 통해 SQL을 실행(execSQL, rawQuery)할 수 있다.

### 테이블 만들기 : CREATE TABLE
SQLiteDatabase 객체의 execSQL 메소드를 호출할 때 파라미터로 SQL문을 전달하면 SQL문이 실행된다.  
```JAVA
private void createTable(String name) {
    println(“creating table [“ + name + “].“);
 
    db.execSQL(“create table “ + name + “(“ 
        + “ _id integer PRIMARY KEY autoincrement, “ 
        + “ name text, “
        + “ age integer, “
        + “ phone text);“ );
    tableCreated = true;
}
```
_id는 내부에서 사용되는 아이디이며 되도록이면 각각의 테이블마다 만들어주는 것이 좋다. 이 칼럼에 들어가는 값을 직접 추가할 필요가 없도록 autoincrement로 지정한다. 이렇게 함으로써 레코드가 추가될 때마다 자동으로 숫자가 입력된다.  
칼럼은 name, age, phone 이라는 이름으로 추가되었으며 각각 text나 integer 타입으로 지정되었다.  
+ SQLite 데이터베이스의 칼럼에 지정할 수 있는 대표적인 타입  
  ```
  문자열 : text, varchar  
  정수 : smallint, integer  
  부동소수 : real, float, double  
  참, 거짓 : boolean  
  시간 및 날짜 : date, time, timestamp  
  바이너리 : blob, binary
  ```

테이블은 한 번 만들어지면 삭제(DROP)하지 않는 이상 동일한 이름으로 만들 수 없다. 따라서 매번 동일한 이 SQL문을 실행하게 되면 기존 테이블이 있다는 에러 메시지가 보이게 된다.  
이전에 이미 만들어진 테이블이 없을 때만 새로운 테이블을 만들도록 하려면 IF NOT EXISTS 키워드를 붙여준다.
```JAVA
CREATE TABLE [IF NOT EXISTS] table_name(col_name column_definition, ...)
[table_option] ...
```

### 데이터 저장 : INSERT INTO
```JAVA
INSERT INTO 테이블명 (칼럼명1, 칼럼명2, 칼럼명3, ... 칼럼명N)  
VALUES (값1, 값2, 값3, ... 값N);
```
데이터의 자료형이 문자열이면 ,를 붙이고 문자열이 아닌 숫자는 ,를 붙이지 않는다. SQL문을 실행할 때는 execSQL 메소드를 호출.
```JAVA
db.execSQL( “insert into employee(name, age, phone) values (‘John‘, 20, ‘010-7788-1234‘“ );
```

### 데이터 조회 : SELECT
```JAVA
SELECT [* | DISTINCT] column_name [,columnname2] 
FROM tablename1 [,tablename2]
WHERE [condition and|or condition...]
[GROUP BY column-list]
[HAVING conditions]
[ORDER BY “column-list“ [ASC | DESC] ]
```
이렇게 만들어진 SQL문은 rawQuery 메소드를 호출하면서 전달한다.
```JAVA
Cursor c1 = db.rawQuery(“select name, age, address from employee“, null);
println(“cursor count : “ + c1.getCount());
```

### 커서 다루기
rawQuery 메소드를 호출했을 때, 반환되는 객체는 Cursor이다. 조회했을 때 반환되는 레코드는 한 개일 수도 있고 여러 개일 수도 있는데 이 Cursor 객체를 이용하면 조회된 레코드를 하나씩 참조하면서 데이터를 꺼내볼 수 있다.  
Cursor 객체는 각각의 레코드를 moveToNext 메소드로 넘겨볼 수 있도록 하며 그 안에 들어있는 칼럼 데이터는 getString, getInt 와 같이 자료형에 맞는 메소드를 이용해 확인할 수 있도록 한다. for문을 사용하면 Cursor에서 참조하는 모든 조회결과를 확인가능하다.
```JAVA
for (int i = 0; i < recordCount; i++) {
    c1.moveToNext();
    String name = c1.getString(0);
    int age = c1.getInt(1);
    String phone = c1.getString(2);
    println(“Record #“ + i + “ : “ + name + “, “ + age + “, “ + phone);
}
```
+ 커서 객체로 칼럼 데이터를 확인할 때 사용할 수 있는 메소드들로는 다음과 같은 것들이 있다.  
  ```JAVA
  public abstract String getString (int columnIndex) 
  public abstract short getShort (int columnIndex) 
  public abstract int getInt (int columnIndex) 
  public abstract long getLong (int columnIndex) 
  public abstract float getFloat (int columnIndex) 
  public abstract double getDouble (int columnIndex) 
  public abstract byte[] getBlob (int columnIndex) 
  ```
  
### Helper
이미 상용화된 앱의 경우 데이터베이스 테이블 구조가 변경되어야 할 때, 새로 앱을 설치하는 경우가 아닌이상 테이블이 이미 만들어져 있기 때문에 테이블을 삭제하고 다시 만들도록 하기가 불가능하다. 이때 사용하는것이 헬퍼 클래스이다. 헬퍼 클래스는 DB의 생성, 오픈, 업그레이드, 다운그레이를 할때 유용하다.  

### Helper class생성 : SQLiteOpenHelper
```JAVA
public SQLiteOpenHelper (Context context, String name, 
			 SQLiteDatabase.CursorFactory factory, int version)
```
이 클래스 안에는 세 개의 메소드가 있으며 데이터베이스의 상태에 따라 자동으로 호출된다.
```JAVA
public abstract void onCreate (SQLiteDatabase db) : 데이터베이스가 만들어지는 경우
public abstract void onOpen (SQLiteDatabase db) 
public abstract void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) 
							: 업그레이드가 필요한 경우
```
구조는 다음과 같다.  
```
[CustomerDatabase
    [DatabaseHelper extends SQLiteOpenHelper
       [onCreate()]
       [onOpen()]
       [onUpgrade()]
    ]
]
```

### 헬퍼 클래스 상속하여 정의하기
SQLiteOpenHelper 클래스를 상속하여 새로운 클래스를 정의하면 데이터베이스를 관리하는 코드를 하나의 클래스 안에서 처리할 수 있다.
```JAVA
private class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
```
onCreate메소드는 데이터베이스가 처음 만들어지는 경우에 호출되므로 데이터베이스를 초기화(생성) 하는 SQL을 넣어준다.
```JAVA
public void onCreate(SQLiteDatabase db) {
    println(“creating table [“ + TABLE_NAME + “].“);
 	
    String CREATE_SQL = “create table “ + TABLE_NAME + “(“ 
        + “ _id integer PRIMARY KEY autoincrement, “ 
        + “ name text, “
        + “ age integer, “
        + “ phone text)“;
    try {
      db.execSQL(CREATE_SQL);
    } catch(Exception ex) {
      Log.e(TAG, “Exception in CREATE_SQL“, ex);
    }
```
onUpgrade 메소드는 테이블이 변경되어야 하는 등 단말에 저장된 데이터베이스의 구조가 바뀌어야 하는 경우에 사용한다. 이 안에는 테이블을 변경하기 위한 ALTER 문 등을 넣을 수 있으며 필요한 경우에는 이미 저장되어 있는 데이터를 다른 곳에 복사했다가 새로 테이블을 만들고 그 테이블에 넣어주는 방식으로 처리하기도 한다.
```JAVA
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(TAG, “Upgrading database from version “ + oldVersion + 
        “ to “ + newVersion + “.“);

    if (newVersion > 1) {
        db.execSQL(“DROP TABLE IF EXISTS “ + TABLE_NAME);
    }
```


>> 위코드형태가 아닌 SQL형태로 작성하는것으로 연습하기
>> RoomDB활용하기
