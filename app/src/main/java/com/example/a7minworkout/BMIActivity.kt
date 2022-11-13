package com.example.a7minworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a7minworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    companion object{
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
        private const val US_UNiTS_VIEW = "US_UNIT_VIEW"
    }
    private var currentVisibleView: String = METRIC_UNITS_VIEW

    private var binding :ActivityBmiBinding?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarBmiActivity)
        if (supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title="CALCULATE BMI"
        }
        binding?.toolbarBmiActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
        makeVisibleMetricUnitsView()

        binding?.rgUnits?.setOnCheckedChangeListener { _, checkedId:Int ->
            if (checkedId==R.id.rbMetricUnits){
                makeVisibleMetricUnitsView()
            }else{
                makeVisibleUSUnitsView()
            }
        }

        binding?.btnCalculateUnits?.setOnClickListener {
            calculateUnits()
        }
    }
    private fun validateMetricUnits():Boolean{
        var isValid = true

        if(binding?.etMetricUnitWeight?.text.toString().isEmpty()){
            isValid = false
        }
        if(binding?.etMetricUnitHeight?.text.toString().isEmpty()){
            isValid = false
        }
        return isValid
    }

    private fun validateUsUnits():Boolean{
        var isValid = true

        when{

            binding?.etUsMetricUnitWeight?.text.toString().isEmpty()->{
                isValid = false
            }
            binding?.etUsMetricUnitHeightFeet?.text.toString().isEmpty()->{
                isValid = false
            }
            binding?.etUsMetricUnitHeightInch?.text.toString().isEmpty()->{
                isValid = false
            }
        }
            return isValid
    }

    private fun makeVisibleMetricUnitsView(){
        currentVisibleView = METRIC_UNITS_VIEW
        binding?.tilMetricUnitWeight?.visibility=View.VISIBLE
        binding?.tilMetricUnitHeight?.visibility=View.VISIBLE
        binding?.tilUsMetricUnitWeight?.visibility=View.GONE
        binding?.tilMetricUsUnitHeightFeet?.visibility=View.GONE
        binding?.tilMetricUsUnitHeightInch?.visibility=View.GONE

        binding?.etMetricUnitHeight?.text!!.clear()
        binding?.etMetricUnitWeight?.text!!.clear()

        binding?.llDisplayBMIResult?.visibility=View.INVISIBLE

    }

    private fun makeVisibleUSUnitsView(){
        currentVisibleView = US_UNiTS_VIEW
        binding?.tilMetricUnitWeight?.visibility=View.INVISIBLE
        binding?.tilMetricUnitHeight?.visibility=View.INVISIBLE
        binding?.tilUsMetricUnitWeight?.visibility=View.VISIBLE
        binding?.tilMetricUsUnitHeightFeet?.visibility=View.VISIBLE
        binding?.tilMetricUsUnitHeightInch?.visibility=View.VISIBLE

        binding?.etUsMetricUnitWeight?.text!!.clear()
        binding?.etUsMetricUnitHeightFeet?.text!!.clear()
        binding?.etUsMetricUnitWeight?.text!!.clear()

        binding?.llDisplayBMIResult?.visibility=View.INVISIBLE

    }

    private fun calculateUnits(){
        if (currentVisibleView== METRIC_UNITS_VIEW){
            if(validateMetricUnits()){
                val heightValue: Float = binding?.etMetricUnitHeight?.text.toString().toFloat()/100

                val weightValue: Float = binding?.etMetricUnitWeight?.text.toString().toFloat()

                val bmi = weightValue/(heightValue*heightValue)
                displayBMIResult(bmi)


            }else{
                Toast.makeText(this@BMIActivity,"please enter valid values",Toast.LENGTH_SHORT).show()
            }
        }else{
            if (validateUsUnits()){
                val usUnitHeightValueFeet : String =
                    binding?.etUsMetricUnitHeightFeet?.text.toString()
                val usUnitHeightValueInch : String =
                    binding?.etUsMetricUnitHeightInch?.text.toString()
                val usUnitWeightValue : Float =
                    binding?.etUsMetricUnitWeight?.text.toString().toFloat()

                val heightValue =
                    usUnitHeightValueInch.toFloat()+usUnitHeightValueFeet.toFloat()*12
                val bmi =703*(usUnitWeightValue/(heightValue*heightValue))

                displayBMIResult(bmi)
            }else{
                Toast.makeText(this@BMIActivity," please enter valid values",Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun displayBMIResult(bmi:Float){
        val bmiLabel: String
        val bmiDescription: String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Dhyan Rakho apna bahot buri condition me ho"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0
        ) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Bahot kam weight he..khana vana khao nhi to nipat jaoge"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0
        ) {
            bmiLabel = "Underweight"
            bmiDescription = "Thora or khao.. abhi bhi underweight hi ho"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0
        ) {
            bmiLabel = "Normal"
            bmiDescription = "Gajab .. Ekdam fit ho Aapto"
        } else if (bmi.compareTo(25f) > 0 && bmi.compareTo(30f)<=0) {
            bmiLabel = "Overweight"
            bmiDescription = "weight increase ho gya hai..you need to take action"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0
        ) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0
        ) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        } else {
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        binding?.llDisplayBMIResult?.visibility = View.VISIBLE

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()
        binding?.tvBMIValue?.text = bmiValue
        binding?.tvBMIType?.text=bmiLabel
        binding?.tvBMIDescription?.text=bmiDescription

    }
}