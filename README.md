# About this Project

An android auto-adaptive layout library

# Core Ability

Auto wrap line when item reach parent end

# Attributes

- itemMarginX, padding between each column
- itemMarginY, padding between each row
- item margins will be ignored, you can use padding or wrapper view instead

# Steps for Integration

#### 1. Dependency

```kotlin
api("io.github.hellogoogle2000:android-flowlayout:1.0.3")
```

#### 2. Apply in Xml Layout

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical">

    <com.android.library.flowlayout.FlowLayout
        android:id="@+id/flowLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:paddingTop="20dp"
        app:itemMarginX="10dp"
        app:itemMarginY="5dp">

        <TextView
            android:layout_width="50dp"
            android:layout_height="30dp"
            android:background="#22FF0000"
            android:gravity="center"
            android:text="1"
            android:textSize="20dp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="80dp"
            android:layout_height="30dp"
            android:background="#22FF0000"
            android:gravity="center"
            android:text="2"
            android:textSize="20dp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="200dp"
            android:layout_height="30dp"
            android:background="#22FF0000"
            android:gravity="center"
            android:text="3"
            android:textSize="20dp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="190dp"
            android:layout_height="30dp"
            android:background="#22FF0000"
            android:gravity="center"
            android:text="4"
            android:textSize="20dp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="40dp"
            android:layout_height="30dp"
            android:background="#22FF0000"
            android:gravity="center"
            android:text="5"
            android:textSize="20dp"
            android:textStyle="bold" />
    </com.android.library.flowlayout.FlowLayout>
</LinearLayout>
```

#### 3. Convert View from Data

you are also enabled to create view from a data set dynamically

```kotlin
val simulated = mutableListOf<String>().apply {
    for (i in 0 until 30)
        add(UUID.randomUUID().toString())
}
binding.flowLayout.applyViewAdapter({ Button(this) }, simulated) { view, data ->
    view.text = data
    view.setPadding(15, 15, 15, 15)
    val lp = view.layoutParams
    lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
}
```
# Preview

<img src="https://github.com/user-attachments/assets/4928f382-8180-47d4-af93-1697335bd197" height="250"><br>
<img src="https://github.com/user-attachments/assets/8d07cdc9-79da-43f5-8c38-59fba53cf0a5" height="250"><br>
<img src="https://github.com/user-attachments/assets/07d80c3d-aad8-42f5-ab16-f88e853c7fad" height="250">
