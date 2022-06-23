package com.dapadz.badgecounterviewapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.dapadz.counterview.CounterBadgeView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val counterDef = findViewById<CounterBadgeView>(R.id.c_d)
        val counterColor = findViewById<CounterBadgeView>(R.id.c_color)
        val counterSize = findViewById<CounterBadgeView>(R.id.c_size)
        val counterMax = findViewById<CounterBadgeView>(R.id.c_max)
        val counterHide = findViewById<CounterBadgeView>(R.id.c_hide)
        val counterPaddings = findViewById<CounterBadgeView>(R.id.c_paddings)

        findViewById<ConstraintLayout>(R.id.root).setOnClickListener {

            counterDef.setValue(counterDef.getValue().plus(1))
            counterColor.setValue(counterColor.getValue().plus(1))
            counterSize.setValue(counterSize.getValue().plus(1))
            counterPaddings.setValue(counterPaddings.getValue().plus(1))
            counterMax.setValue(counterMax.getValue().plus(1))

            if (counterHide.getValue() <= 0)
                counterHide.setValue(5)
            else
                counterHide.setValue(counterHide.getValue().minus(1))

        }

        findViewById<ConstraintLayout>(R.id.root).setOnLongClickListener {

            counterDef.setValue(0)
            counterColor.setValue(0)
            counterSize.setValue(0)
            counterPaddings.setValue(15)
            counterHide.setValue(5)
            counterMax.setValue(0)

            true
        }
    }
}