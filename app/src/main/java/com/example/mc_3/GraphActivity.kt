package com.example.mc_3

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mc_3.model.OrientationViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class GraphActivity : AppCompatActivity() {

    // ViewModel 초기화
    //private val viewModel: OrientationViewModel by viewModels()
    private lateinit var viewModel: OrientationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        Log.d("tag","graph error")

        viewModel = ViewModelProvider(this).get(OrientationViewModel::class.java)

        Log.d("tag","after model error")

        populateGraphs()
    }

    private fun populateGraphs() {
        // LiveData 관찰
        viewModel.allOrientations.observe(this) { orientationDataList ->
            val entriesX = ArrayList<Entry>()
            val entriesY = ArrayList<Entry>()
            val entriesZ = ArrayList<Entry>()

            Log.d("tag","populate error")

            orientationDataList.forEachIndexed { index, orientationData ->
                entriesX.add(Entry(index.toFloat(), orientationData.xAngle))
                entriesY.add(Entry(index.toFloat(), orientationData.yAngle))
                entriesZ.add(Entry(index.toFloat(), orientationData.zAngle))
            }

            val dataSetX = LineDataSet(entriesX, "X Angle")
            val dataSetY = LineDataSet(entriesY, "Y Angle")
            val dataSetZ = LineDataSet(entriesZ, "Z Angle")


            Log.d("tag","pop 2 error")

            findViewById<LineChart>(R.id.chartAzimuth).data = LineData(dataSetX)
            findViewById<LineChart>(R.id.chartPitch).data = LineData(dataSetY)
            findViewById<LineChart>(R.id.chartRoll).data = LineData(dataSetZ)

            Log.d("tag","pop 3 error")

            listOf(R.id.chartAzimuth, R.id.chartPitch, R.id.chartRoll).forEach {
                findViewById<LineChart>(it).invalidate() // 차트 새로고침
            }
        }
    }
}
