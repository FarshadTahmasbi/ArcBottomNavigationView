
# ArcBottomNavigationView
[ ![Download](https://api.bintray.com/packages/androidisland/ArcBottomNavigationView/arcbottomnavigationview/images/download.svg)](https://bintray.com/androidisland/ArcBottomNavigationView/arcbottomnavigationview/_latestVersion)

Custom version of `BottomNavigationView` with a curved appearance
![](raw/arcbottomnav.png)

## Gradle

Add maven repository to build.gradle root level

	allprojects {
		repositories {
			maven { url "https://androidisland.bintray.com/ArcBottomNavigationView" }
		}
	}
  
Then add this to build.gradle in app module
  
  	dependencies {
	        implementation 'com.androidisland.views:arcbottomnavigationview:1.0.0'
	}

## Usage

    <com.androidisland.views.ArcBottomNavigationView  
      android:id="@+id/arc_bottom_nav"  
      android:layout_width="0dp"  
      android:layout_height="wrap_content"  
      android:layout_gravity="bottom"  
      android:background="#4CAF50"  
      app:ai_buttonBackgroundTint="#E91E63"  
      app:ai_buttonIcon="@drawable/ic_create_black_24dp"  
      app:ai_buttonIconSize="32dp"  
      app:ai_buttonIconTint="#FFF"  
      app:ai_buttonRippleColor="#FFF"  
      app:ai_buttonSize="56dp"  
      app:ai_buttonStrokeColor="#E91E63"  
      app:ai_buttonStrokeWidth="0dp"  
      app:ai_selectableButton="false"  
      app:ai_state="arc"  
      app:itemIconTint="@drawable/selector_icon_tint"  
      app:itemTextColor="#fff"  
      app:layout_constraintBottom_toBottomOf="parent"  
      app:layout_constraintLeft_toLeftOf="parent"  
      app:layout_constraintRight_toRightOf="parent"  
      app:menu="@menu/bottom_nav_items" />

## License

    Copyright 2019 Farshad Tahmasbi
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
