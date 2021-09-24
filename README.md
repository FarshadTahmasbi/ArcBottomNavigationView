
---

### NOTE: This library is no longer maintained.

---

# ArcBottomNavigationView 

Custom version of `BottomNavigationView` with a curved appearance


![](raw/arcbottomnav.png)


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
      
|*attribute*| *description* |
|--|--|
| `ai_state`| current state of view,`flat` or `arc`|
| `ai_buttonSize`| material button size|
| `ai_buttonMargin`| material button margin with parent borders|
| `ai_buttonBackgroundTint`| material button background tint color|
| `ai_buttonStrokeColor`| material button stroke color|
| `ai_buttonStrokeWidth`| material button stroke width|
| `ai_buttonRippleColor`| material button ripple effect color|
| `ai_buttonIcon`| material button icon drawable|
| `ai_buttonIconSize`| material button icon size|
| `ai_buttonIconTint`| material button icon tint color|
| `ai_fontPath`| menu items typeface (path of fonts copied to assets folder)|
| `ai_selectableButton`| if set it to `true` when user clicks button, the current item will be deselected|


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
