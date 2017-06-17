# CoolWeather
## 代码来源于《第一行代码》里的酷欧天气
1. 创建数据库和表
	* 使用了LitePal数据库。
	  * 在Assets目录下配置litepal.xml文件。
	  * AndroidManifest.xml中添加
	  `` android:name="org.litepal.LitepalApplication"``
	* db包下创建了省，市，县的表。
2. 获取省市县的列表
 	1. 通过网络请求处理JSon数据
 	2. 将列表信息存入数据库
 	3. 重新读取数据库里面信息
 	4. 显示在ListView
3. 显示天气信息
	1. 定义GSON实体类
	2. 编写天气界面
	3. 将天气信息显示在界面上
		* 如果没有缓存数据获取列表
		* 然后请求地区的天气信息
		* 点击item出现地区的天气信息并将信息写入缓存
	4. 添加必应图片
		```java
        if (Build.VERSION.SDK_INT >=21) {
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
		```
		```
		//为状态栏留出空间
		 android:fitsSystemWindows="true" 
		```
