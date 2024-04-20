package com.example.mc_3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mc_3.Database.OrientationData
import com.example.mc_3.databinding.ActivityMainBinding
import com.example.mc_3.model.OrientationViewModel
import java.io.File
import java.io.IOException



class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometerSensor: Sensor
    private lateinit var magneticFieldSensor: Sensor
    private lateinit var viewModel: OrientationViewModel

    private var accelerometerData = FloatArray(3)
    private var magneticFieldData = FloatArray(3)
    private var rotationMatrix = FloatArray(9)
    private var orientationAngles = FloatArray(3)

    companion object {
        private const val REQUEST_WRITE_STORAGE = 1
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        viewModel = ViewModelProvider(this).get(OrientationViewModel::class.java)
//
//        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
//        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!!
//
//        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
//        sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL)
//
//        Log.d("tag","before error")
//
//        val button = findViewById<Button>(R.id.button_show_graph)
//        button.setOnClickListener {
//            Log.d("tag","after button  error")
//            val intent = Intent(this, GraphActivity::class.java)
//
//            Log.d("tag","after button 2 error")
//            startActivity(intent)
//            Log.d("tag","after button 3 error")
//        }
//
//        findViewById<Button>(R.id.button_export_csv).setOnClickListener {
//            exportDataToCSV()
//        }
//
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()

        viewModel = ViewModelProvider(this).get(OrientationViewModel::class.java)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!!

        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL)

        // Set up UI controls and listeners
        setupSensorDelaySpinner()  // Call this method here

        val button = findViewById<Button>(R.id.button_show_graph)
        button.setOnClickListener {
            val intent = Intent(this, GraphActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.button_export_csv).setOnClickListener {
            exportDataToCSV()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    System.arraycopy(it.values, 0, accelerometerData, 0, accelerometerData.size)
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    System.arraycopy(it.values, 0, magneticFieldData, 0, magneticFieldData.size)
                }
            }

            val success = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData, magneticFieldData)
            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientationAngles)

                // Convert radians to degrees
                val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                val pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                val roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

                // Update UI
                binding.xAngle.text = "X (Azimuth): ${azimuth}°"
                binding.yAngle.text = "Y (Pitch): ${pitch}°"
                binding.zAngle.text = "Z (Roll): ${roll}°"

                // Save data to database
                val orientationData = OrientationData(0, System.currentTimeMillis(), azimuth, pitch, roll)
                viewModel.insert(orientationData)
            }
        }
    }

    private fun exportDataToCSV() {
        viewModel.allOrientations.observe(this) { dataList ->
            val folder = getExternalFilesDir(null)
            val csvFile = File(folder, "orientation_data.csv")
            try {
                csvFile.bufferedWriter().use { out ->
                    out.write("ID,TimeStamp,X_Angle,Y_Angle,Z_Angle\n")
                    dataList.forEach {
                        out.write("${it.id},${it.timeStamp},${it.xAngle},${it.yAngle},${it.zAngle}\n")
                    }
                }
                Toast.makeText(this, "Data exported to ${csvFile.absolutePath}", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                Toast.makeText(this, "Failed to export  ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle sensor accuracy changes if needed
    }

    // Don't forget to unregister the sensors when the app is paused or stopped
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSensorDelaySpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            arrayOf("Normal", "UI", "Game", "Fastest")
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        findViewById<Spinner>(R.id.spinner_sensor_delay).apply {
            this.adapter = adapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val delay = when (position) {
                        0 -> SensorManager.SENSOR_DELAY_NORMAL
                        1 -> SensorManager.SENSOR_DELAY_UI
                        2 -> SensorManager.SENSOR_DELAY_GAME
                        3 -> SensorManager.SENSOR_DELAY_FASTEST
                        else -> SensorManager.SENSOR_DELAY_NORMAL
                    }
                    sensorManager.unregisterListener(this@MainActivity)
                    sensorManager.registerListener(this@MainActivity, accelerometerSensor, delay)
                    sensorManager.registerListener(this@MainActivity, magneticFieldSensor, delay)
                    Toast.makeText(this@MainActivity, "Sensor delay updated", Toast.LENGTH_SHORT).show()
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }


}


