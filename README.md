					-------------------- Today's Topic ---------------------
									(1) Web과 통신하기
					--------------------------------------------------------


 - 프로젝트 명 : [HttpUrlConnection]

 - 내용 : 인터넷 홈페이지의 소스를 받아서 화면에 출력합니다.[HttpUrlConnection]	


# 1. 웹과 통신하기 - [HttpUrlConnection]

이번 시간에는 http 통신을 가지고 실제로 웹의 데이터를 받는 법을 배우도록 하겠습니다.

## 출력화면

![](http://i.imgur.com/9OjOYzv.png)

![](http://i.imgur.com/RM9axMh.png)

유저가 브라우저를 입력하고 GET URL 버튼을 누르면 해당 화면의 소스를 받아서 출력합니다.

프로젝트는 MainActivity로만 구성되어 있기 때문에 클래스 내부에서 정의한 메소드 동작 위주로 설명드리겠습니다.

## 1.1 인터넷 permission 

				<uses-permission android:name="android.permission.INTERNET"/>

인터넷 작업환경이 필요하기 때문에 AndroidManifest.xml 파일에 위 명령을 넣어주어 권한설정을 해줍니다.


## 1.2 AsyncTask

--> 사용자가 입력한 url 주소의 웹정보를 가져오기 위해서는 메인쓰레드가 아니라 서브쓰레드에서 작업해주어야 합니다. 또한 받은 정보값을 TextView에 출력해주어야 합니다. 이 때 사용할 수 있는 것이 AsyncTask 입니다.

[MainActivity.java]

![](http://i.imgur.com/L0dqgS3.png)
![](http://i.imgur.com/pE4Jadr.png)
![](http://i.imgur.com/oqI68Zp.png)

(1) 사용자로부터 받은 urlString에 "http://"이 안되어 있다면 "http://"를 포함시켜 줍니다. 이 스트링은 AsyncTask.execute()의 패러미터로 넘겨줄 값입니다.

        if(!urlString.startsWith("http")){                       
            urlString = "http://" + urlString;
        }

(2) doInBackground()에서는 url로 네트워크 연결을 한다음 웹에서 필요한 소스를 가져옵니다.

                    // 1. String을 url 객체로 변환
                    URL url = new URL(urlString); 
					// 2. url 로 네트워크 연결 시작  (서버랑 연결!)
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();                      
					 // 3. url 연결에 대한 옵션 설정 (서버랑 연결하는 방식 결정!)
                    connection.setRequestMethod("GET"); 




>> 그외 서버랑 연결하는 옵션들은??
>
>>  GET : 데이터 요청시 사용하는 방식
>>  POST : 데이터 입력시 사용하는 방식
>>  PUT : 데이터 수정시 사용하는 방식
>>  DELETE : 데이터 삭제시 사용하는 방식 
    
					// 4. 서버로부터 응답코드 회신
                    int responseCode = connection.getResponseCode();
					// 데이터가 정상적으로 들어왔는지를 확인합니다.
					if (responseCode == HttpURLConnection.HTTP_OK) 	


                        // 4.1 서버연결로부터 스트림을 얻는다.
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        // 4.2 스트링 빌더는 스트링 연산을 빠르게 해준다.
                        StringBuilder result = new StringBuilder();
                        String lineOfData = "";

						// 4.3 반복문을 돌면서 버퍼의 데이터를 읽어온다.
                        while ( (lineOfData = br.readLine()) != null) {

                            result.append(lineOfData);
                        }

(3) onPostExecute()에서는 Web에서 받아서 저장한 result를 화면의 textView에 세팅합니다.

               // 결과값 메인 UI에 세팅팅
               textResult.setText(result);

(4) execute() : AsyncTask를 실행시킵니다. urlString으로 주소정보를 같이 넘깁니다.	




## 1.3 프로그레스바 설정

![](http://i.imgur.com/wHzC5HZ.png)

프로그레스바를 사용하기 위해서 Layout을 이용하는 방법 / Layout 없이 프로그레스바를 생성하는 방법이 있습니다.

## 1.3.1  Layout을 이용하는 방법 

[activity_main.xml]

![](http://i.imgur.com/95oTrdz.png)

--> 먼저 Relativelayout으로 레이아웃을 생성한 다음 프로그레스바 위젯을 추가합니다.

[MainActivity.java]

![](http://i.imgur.com/Brh39DU.png)

--> 웹에서 소스를 가져올 동안만 프로그레스바를 사용하기 때문에 onPreExecute()에서 프로그레스바를 "VISIBLE" 모드로 설정하고 onPostExecute "GONE"모드로 화면상에서 안보이게 합니다.


## 1.3.2 Layout 없이 프로그레스바를 생성하는 방법

--> ProgressDialog 객체로 프로그레스바를 생성합니다.

		ProgressDialog dialog = new ProgressDialog(MainActivity.this);

![](http://i.imgur.com/Im6nvzW.png)

###### onPreExecute() : 먼저 프로그레스바의 모양과 메시지를 설정하고 화면에 띄웁니다.

![](http://i.imgur.com/TlG8495.png)

###### onPostExecute() : 웹에서 소스를 다 받아왔기 때문에 dismiss() 메소드로 프로그레스바를 종료시킵니다.

## 1.4 태그 값 가져오기

![](http://i.imgur.com/jF7aKRF.png)

![](http://i.imgur.com/nLOzaQM.png)

--> 웹에서 소스로 가져온 html 문서에서 필요한 내용만을 가져옵니다. 여기서는 <title> 태그 안에 있는 내용만을 선별해서 텍스트뷰에 출력합니다.

### 1.4.1 코드

--> onPostExecute()에 아래 코드를 추가합니다. <title> 태그가 있는 위치를 불러와서 원하는 내용을 substring()으로 가져옵니다.

                // title 태그값 추출하기
                String title = result.substring(result.indexOf("<title>") + 7, result.indexOf("</title>"));
                textTitle.setText(title);
				