# Badge Counter View

A simple library that adds an animated counter.

![preview](https://github.com/tompadz/BadgeCounterView/blob/master/info/ezgif-3-97fecc12f4.gif?raw=true)


# Installation

Add it in your root build.gradle at the end of repositories:

   
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Add the dependency
    
    
	dependencies {
	    implementation 'com.github.tompadz:BadgeCounterView:1.0.1'
	}			
    
# Usage

using xml

    <com.dapadz.counterview.CounterBadgeView  
	  android:layout_width="wrap_content"  
	  android:layout_height="wrap_content"  
	  app:counterTextSize="12s111p"  
	  app:counterShowIfZero="true"  
	  app:counterMaxValue="10"  
	  app:counterViewDefaultValue="0"  
	  app:counterViewTextColor="@color/white"  
	  app:counterViewBackgroundColor="@color/black"  
	  app:counterPaddingHorizontal="10dp"  
	  app:counterPaddingVertical="5dp">

| `CODE` |  Description |
|--|--|
| `setPadding(vertical : Float?, horizontal : Float?)` |Sets indents for the counter text |
| `setTextSize(size:Float)` |  Sets the text size of the counter|
| `setCounterBackgroundColor(color:Int)` |  Sets the background of the counter |
| `setCounterBackgroundColor(color:String)` |  Sets the background of the counter |
| `setMaxCounterValue(value:Int)` |  Sets the maximum value for the counter |
| `setDefaultCounterValue(value:Int)` |  Sets the default value for the counter |
| `setShowIfValueZero(state:Boolean)` |  Whether to display the counter if its value is less than or equal to zero |
| `setCounterTextColor(color:Int)` |  Sets the text color of the counter |
| `setCounterTextColor(color:String)` |  Sets the text color of the counter |
