
package learn.codeacademy.covidtracker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.spinner_item.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Runnable
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var Jobject = JSONObject()
    var recovered = String()
    var deceased = String()
    var xvalue = ArrayList<String>()
    var lineentryconfirmed = ArrayList<Entry>()
    var lineentry = ArrayList<Entry>()
    var lineentrytested = ArrayList<Entry>()
    var lineentrydeceased = ArrayList<Entry>()
    var finaldataset = ArrayList<LineDataSet>()

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //An adapter is created for spinner here so we can display all items to be selected in the spinner
        var adapter = ArrayAdapter.createFromResource(
                this,
                //R.array.states is an arraylist that stores the name of all the states
                R.array.States,
                //R.layout.spinner_item is the spinner widget itself
                R.layout.spinner_item
        )
        //Adapter is assigned to spinner here
        spinner.adapter = adapter
        spinner.onItemSelectedListener = (this)
//Handles India covid timeline and returns graphs for whole of the country
        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(3))
            withContext(Dispatchers.Main) {
                Thread {
                    kotlin.run {
                        val client = OkHttpClient().newBuilder()
                                .build()
                        val request = Request.Builder()
                                .url("https://corona-virus-world-and-india-data.p.rapidapi.com/api_india_timeline")
                                .method("GET", null)
                                .addHeader(
                                        "X-RapidAPI-Key",
                                        "3cf87b7362mshb0aa4e5238acaaep139e9ajsn60193dcabdd2"
                                )
                                .addHeader(
                                        "X-RapidAPI-Host",
                                        "corona-virus-world-and-india-data.p.rapidapi.com"
                                )
                                .build()
                        val response: Response = client.newCall(request).execute()
                        when {
                            response.isSuccessful -> {
                                Thread {
                                    kotlin.run {
                                        val data = response.body()!!.string()
                                        val xvalue = ArrayList<String>()
                                        val lineentry = ArrayList<Entry>()
                                        val lineentry1 = ArrayList<Entry>()
                                        val xvalue1 = ArrayList<String>()
                                        val lineentryalt = ArrayList<Entry>()
                                        val lineentry1alt = ArrayList<Entry>()
                                        val font = ResourcesCompat.getFont(this@MainActivity, R.font.gilroy_bold)
                                        val blackcolor = ContextCompat.getColor(applicationContext, R.color.black)
                                        val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                                        val redcolor = ContextCompat.getColor(applicationContext, R.color.red)
                                        val bluecolor = ContextCompat.getColor(applicationContext, R.color.confirmed)
                                        val finalbluecolor = ContextCompat.getColor(applicationContext, R.color.finalblue)
                                        Log.d("TAG", "$data")
                                        val Jarray = JSONArray(data)
                                        var length = Jarray.length()
                                        for (i in 0 until length) {
                                            runOnUiThread(kotlinx.coroutines.Runnable {
                                                val filter = Jarray.getJSONObject(i)
                                                //This stores the daily dates
                                                val dates = filter.getString("dateymd")
                                                //linechartproperties function is called here
                                                linechartproperties(finalbluecolor, bluecolor, greencolor, font!!, blackcolor)
                                                //totalconfirmed,dailyconfirmed etc. case numbers are stored here
                                                val totalconfirmed = filter.getString("totalconfirmed")
                                                val dailyconfirmed = filter.getString("dailyconfirmed")
                                                val totalrecovered = filter.getString("totalrecovered")
                                                val totaldeceased = filter.getString("totaldeceased")
                                                //xvalue and xvalue1 store dates within themselves in order to show it in graph
                                                xvalue.add(dates)
                                                xvalue1.add(dates)
                                                //lineentry and lineentry1 store the covid data as needed
                                                lineentry.add(Entry(totalconfirmed.toFloat(), i))
                                                lineentry1.add(Entry(dailyconfirmed.toFloat(), i))
                                                lineentryalt.add(Entry(totalrecovered.toFloat(), i))
                                                lineentry1alt.add(Entry(totaldeceased.toFloat(), i))
                                                val linedataset = LineDataSet(lineentry, "Total Confirmed Cases")
                                                val linedataset1 = LineDataSet(lineentry1, "Daily Confirmed Cases")
                                                val linedatasetalt = LineDataSet(lineentryalt, "Total Recovered")
                                                val linedataset1alt = LineDataSet(lineentry1alt, "Total Deaths")
                                                //For second graph
                                                //styling properties are set here
                                                linedatasetalt.color = greencolor
                                                linedatasetalt.circleRadius = 0f
                                                linedatasetalt.setDrawFilled(true)
                                                linedatasetalt.fillColor = greencolor
                                                linedataset1alt.color = redcolor
                                                linedataset1alt.circleRadius = 0f
                                                linedataset1alt.setDrawFilled(true)
                                                linedataset1alt.fillColor = redcolor
                                                //finaldatasetalt stores data for two labels that will be displayed in graph
                                                val finaldatasetalt = ArrayList<LineDataSet>()
                                                finaldatasetalt.add(linedatasetalt)
                                                finaldatasetalt.add(linedataset1alt)
                                                val dataalt = LineData(xvalue1, finaldatasetalt as List<ILineDataSet>?)
                                                //data is assigned hr in order to display it in graph
                                                lineChart2.data = dataalt


                                                //For first graph
                                                linedataset1.color = bluecolor
                                                linedataset1.circleRadius = 0f
                                                linedataset1.setDrawFilled(true)
                                                linedataset1.fillColor = bluecolor
                                                linedataset.color = finalbluecolor
                                                linedataset.circleRadius = 0f
                                                linedataset.valueTypeface = font
                                                linedataset1.valueTypeface = font
                                                linedataset.setDrawFilled(true)
                                                linedataset.fillColor = finalbluecolor
                                                val finaldataset = ArrayList<LineDataSet>()
                                                finaldataset.add(linedataset)
                                                finaldataset.add(linedataset1)
                                                val data = LineData(xvalue, finaldataset as List<ILineDataSet>?)
                                                lineChart.data = data
                                                runOnUiThread(Runnable {
                                                    //.animateXY displays animation for both graphs
                                                    lineChart.animateXY(3000, 3000)
                                                    lineChart2.animateXY(3000, 3000)
                                                    //inflates marker layout needed to display numbers when tapped
                                                    val markerView = CustomMarker(this@MainActivity, R.layout.marker_view)
                                                    //Marker is assigned here that displays numbers when tapped
                                                    lineChart.markerView = markerView
                                                    lineChart2.markerView = markerView
                                                })
                                            })
                                        }
                                    }
                                }.start()
                            }
                        }
                    }
                }.start()
            }
        }
        //This thread handles api that returns total count of confirmed,active,recovered and death cases

        Thread {
            kotlin.run {
                val client = OkHttpClient()

                val request = Request.Builder()
                        .url("https://corona-virus-world-and-india-data.p.rapidapi.com/api_india")
                        .get()
                        .addHeader(
                                "x-rapidapi-key",
                                "3cf87b7362mshb0aa4e5238acaaep139e9ajsn60193dcabdd2"
                        )
                        .addHeader(
                                "x-rapidapi-host",
                                "corona-virus-world-and-india-data.p.rapidapi.com"
                        )
                        .build()

                val response = client.newCall(request).execute()
                when {
                    response.isSuccessful -> {
                        Thread {
                            kotlin.run {
                                val data = response.body()!!.string()
                                val arraylist = ArrayList<String>()
                                Log.d("TAG", "needed$data")
                                val Jobject = JSONObject(data)
                                val statewise = Jobject.getJSONObject("state_wise")
                                Log.d("TAG", "$statewise")
                                val cases = Jobject.getJSONObject("total_values")
                                val confirmedcases = cases.getString("confirmed")
                                val deltaconfirmedcases = cases.getString("deltaconfirmed")
                                val deltadeaths = cases.getString("deltadeaths")
                                val deltarecovered = cases.getString("deltarecovered")
                                val activecases = cases.getString("active")
                                val recoveredcases = cases.getString("recovered")
                                val deathcases = cases.getString("deaths")
                                runOnUiThread(Runnable {
                                    //Variables such as activecases etc. store and update the total count of active cases,confirmed cases etc. daily from the api
                                    Activesubtxt.text = "$activecases"
                                    confirmedsubtxt.text = "$confirmedcases"
                                    Recoveredsubtxt.text = "$recoveredcases"
                                    Deathssubtxt.text = "$deathcases"
                                    //delta variables for eg. deltaconfirmedcases updates the new count of confirmed cases,recovered and deaths daily in respective textviews
                                    confirmdelta.text = "(+$deltaconfirmedcases)"
                                    recoverdelta.text = "(+$deltarecovered)"
                                    deathdelta.text = "(+$deltadeaths)"

                                })
                            }
                        }.start()

                    }
                }
            }
        }.start()

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    //onItemSelected and onNothingSelected handles the spinner events when spinner is selected and when it's not selected
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var position = spinner.selectedItemPosition
        var spinnertxt = spinnertxt
        spinner.setSelection(-1)
        //This thread retrieves daily statewise data of covid cases and updates the deaths,recoveries,people tested etc. daily
        Thread {
            kotlin.run {
                val client = OkHttpClient()

                val request = Request.Builder()
                        .url("https://api.covid19india.org/v4/min/timeseries.min.json")
                        .get().build()

                val response = client.newCall(request).execute()
                when {
                    response.isSuccessful -> {
                        Thread {
                            kotlin.run {
                                val data = response.body()!!.string()
                                Log.d("TAG", "$data")
                                val array = JSONArray()

                                Jobject = JSONObject(data)
                                xvalue = ArrayList<String>()
                                lineentry = ArrayList<Entry>()
                                finaldataset = ArrayList<LineDataSet>()
                                lineentrydeceased = ArrayList<Entry>()
                                lineentrytested = ArrayList<Entry>()
                                lineentryconfirmed = ArrayList<Entry>()
                            }


                        }.start()
                    }
                }
            }
        }.start()
        //we put the spinner position variable in when loop,so for eg. when we select Maharashtra i.e position 1 on spinner,we can accordingly retrieve data for it
        when (position) {
            //As the spinner defaultely selects zero, we again call the thread which retrieves the covid data of india in total since we want to show it as our default case
            0 -> {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(TimeUnit.SECONDS.toMillis(3))
                    withContext(Dispatchers.Main) {
                        Thread {
                            kotlin.run {
                                val client = OkHttpClient().newBuilder()
                                        .build()
                                val request = Request.Builder()
                                        .url("https://corona-virus-world-and-india-data.p.rapidapi.com/api_india_timeline")
                                        .method("GET", null)
                                        .addHeader(
                                                "X-RapidAPI-Key",
                                                "3cf87b7362mshb0aa4e5238acaaep139e9ajsn60193dcabdd2"
                                        )
                                        .addHeader(
                                                "X-RapidAPI-Host",
                                                "corona-virus-world-and-india-data.p.rapidapi.com"
                                        )
                                        .build()
                                val response: Response = client.newCall(request).execute()
                                when {
                                    response.isSuccessful -> {
                                        Thread {
                                            kotlin.run {
                                                val data = response.body()!!.string()
                                                val xvalue = ArrayList<String>()
                                                val lineentry = ArrayList<Entry>()
                                                val lineentry1 = ArrayList<Entry>()
                                                val xvalue1 = ArrayList<String>()
                                                val lineentryalt = ArrayList<Entry>()
                                                val lineentry1alt = ArrayList<Entry>()
                                                val font = ResourcesCompat.getFont(this@MainActivity, R.font.gilroy_bold)
                                                val blackcolor = ContextCompat.getColor(applicationContext, R.color.black)
                                                val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                                                val redcolor = ContextCompat.getColor(applicationContext, R.color.red)
                                                val bluecolor = ContextCompat.getColor(applicationContext, R.color.confirmed)
                                                val finalbluecolor = ContextCompat.getColor(applicationContext, R.color.finalblue)
                                                Log.d("TAG", "$data")
                                                val Jarray = JSONArray(data)
                                                var length = Jarray.length()
                                                for (i in 0 until length) {
                                                    runOnUiThread(kotlinx.coroutines.Runnable {
                                                        val filter = Jarray.getJSONObject(i)
                                                        val dates = filter.getString("dateymd")
                                                        linechartproperties(finalbluecolor, bluecolor, greencolor, font!!, blackcolor)
                                                        val totalconfirmed = filter.getString("totalconfirmed")
                                                        val dailyconfirmed = filter.getString("dailyconfirmed")
                                                        val totalrecovered = filter.getString("totalrecovered")
                                                        val totaldeceased = filter.getString("totaldeceased")
                                                        xvalue.add(dates)
                                                        xvalue1.add(dates)
                                                        lineentry.add(Entry(totalconfirmed.toFloat(), i))
                                                        lineentry1.add(Entry(dailyconfirmed.toFloat(), i))
                                                        lineentryalt.add(Entry(totalrecovered.toFloat(), i))
                                                        lineentry1alt.add(Entry(totaldeceased.toFloat(), i))
                                                        val linedataset = LineDataSet(lineentry, "Total Confirmed Cases")
                                                        val linedataset1 = LineDataSet(lineentry1, "Daily Confirmed Cases")
                                                        val linedatasetalt = LineDataSet(lineentryalt, "Total Recovered")
                                                        val linedataset1alt = LineDataSet(lineentry1alt, "Total Deaths")
                                                        //For second graph
                                                        linedatasetalt.color = greencolor
                                                        linedatasetalt.circleRadius = 0f
                                                        linedatasetalt.setDrawFilled(true)
                                                        linedatasetalt.fillColor = greencolor
                                                        linedataset1alt.color = redcolor
                                                        linedataset1alt.circleRadius = 0f
                                                        linedataset1alt.setDrawFilled(true)
                                                        linedataset1alt.fillColor = redcolor
                                                        val finaldatasetalt = ArrayList<LineDataSet>()
                                                        finaldatasetalt.add(linedatasetalt)
                                                        finaldatasetalt.add(linedataset1alt)
                                                        val dataalt = LineData(xvalue1, finaldatasetalt as List<ILineDataSet>?)
                                                        lineChart2.data = dataalt


                                                        //For first graph
                                                        linedataset1.color = bluecolor
                                                        linedataset1.circleRadius = 0f
                                                        linedataset1.setDrawFilled(true)
                                                        linedataset1.fillColor = bluecolor
                                                        linedataset.color = finalbluecolor
                                                        linedataset.circleRadius = 0f
                                                        linedataset.valueTypeface = font
                                                        linedataset1.valueTypeface = font
                                                        linedataset.setDrawFilled(true)
                                                        linedataset.fillColor = finalbluecolor
                                                        val finaldataset = ArrayList<LineDataSet>()
                                                        finaldataset.add(linedataset)
                                                        finaldataset.add(linedataset1)
                                                        val data = LineData(xvalue, finaldataset as List<ILineDataSet>?)
                                                        lineChart.data = data
                                                        runOnUiThread(Runnable {
                                                            lineChart.animateXY(3000, 3000)
                                                            lineChart2.animateXY(3000, 3000)
                                                            val markerView = CustomMarker(this@MainActivity, R.layout.marker_view)
                                                            lineChart.markerView = markerView
                                                            lineChart2.markerView = markerView
                                                        })
                                                    })
                                                }
                                            }
                                        }.start()
                                    }
                                }
                            }
                        }.start()
                    }
                }
//If nothing is selected,the spinner text is set to GRAY
                spinnertxt.setTextColor(Color.GRAY)
            }
            1 -> {
                //The statename variable changes with every selection,for eg. you choose karnataka you'll mention Jobject.getJSONObject("KA") in statename variable
                val statename = Jobject.getJSONObject("MH")
                val dates = statename.getJSONObject("dates")
                val refineddates = dates.names()
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    //All dates are added and stored in the xvalue variable
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
                    //linentryconfirmed stores confirmed cases
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        //linentry stores recovered cases
                        lineentry.add(Entry(recovered.toFloat(), i))

                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        //lineentrytested stores tested numbers
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    //All retrieve values have been put in datasets in order to show them on graph
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")

//All styling properties are put in respective lines
                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        //In order to show two lines on same graph we have to add them in a common variable i.e finaldataset in this case
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        //datasecondgraph variable stores data for the second graph
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        //datafirst variable stores data for the first graph
                        val datafirst = LineData(xvalue, linedatasettested)

//The data is assigned to respective graphs in order to display the graph properly
                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    //.animateXY helps animate the graph on display
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })

            }
            2 -> {
                val statename = Jobject.getJSONObject("TN")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            3 -> {
                val statename = Jobject.getJSONObject("DL")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            4 -> {
                val statename = Jobject.getJSONObject("KL")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            5 -> {
                val statename = Jobject.getJSONObject("TG")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            6 -> {
                val statename = Jobject.getJSONObject("UP")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            7 -> {
                val statename = Jobject.getJSONObject("AP")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            8 -> {
                val statename = Jobject.getJSONObject("RJ")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            9 -> {
                val statename = Jobject.getJSONObject("MP")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 3) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            10 -> {
                val statename = Jobject.getJSONObject("KA")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "karnataka$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 3) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            11 -> {
                val statename = Jobject.getJSONObject("GJ")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            12 -> {
                val statename = Jobject.getJSONObject("JK")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            13 -> {
                val statename = Jobject.getJSONObject("HR")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            14 -> {
                val statename = Jobject.getJSONObject("PB")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            15 -> {
                val statename = Jobject.getJSONObject("WB")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 3) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            16 -> {
                val statename = Jobject.getJSONObject("BR")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))
                    var recovered = String()

                    if (total.length() > 3) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            17 -> {
                val statename = Jobject.getJSONObject("AS")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))
                    var recovered = String()

                    if (total.length() > 3) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            18 -> {
                val statename = Jobject.getJSONObject("UT")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))
                    var recovered = String()

                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            19 -> {
                val statename = Jobject.getJSONObject("OR")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))
                    var recovered = String()

                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            20 -> {
                val statename = Jobject.getJSONObject("CH")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))
                    var recovered = String()

                    if (total.length() > 3) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 2) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            21 -> {
                val statename = Jobject.getJSONObject("LD")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "ladakh$total")
//
//                                   
//                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))
                    var recovered = String()


                    if (total.length() > 2) {
                        val confirmed = total.getString("confirmed")
                        lineentryconfirmed.add(Entry(confirmed.toFloat(), i))
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))
//                        deceased = total.getString("deceased")
//                        lineentrydeceased.add(Entry(deceased.toFloat(),i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            22 -> {
                val statename = Jobject.getJSONObject("AN")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 2) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            23 -> {
                val statename = Jobject.getJSONObject("CT")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            24 -> {
                val statename = Jobject.getJSONObject("GA")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "GOA$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))

                    if (total.length() > 1) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 3) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            25 -> {
                val statename = Jobject.getJSONObject("HP")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "HP$total")



                    if (total.length() > 1) {
                            val confirmed = total.getString("confirmed")
                        lineentryconfirmed.add(Entry(confirmed.toFloat(), i))
//                                   
                    }
                    if (total.length() > 2) {

//
//
                    }
                    if (total.length() > 4) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                        Log.d("TAG", "${recovered}")
                        Log.d("TAG", "${recovered.length}")
//                        deceased = total.getString("deceased")
//                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
//                        val vaccinated = total.remove("vaccinated")
                    }
                    if (total.length() > 5) {

                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            26 -> {
                val statename = Jobject.getJSONObject("PY")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 2) {
                        recovered = total.getString("recovered")
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {

                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            27 -> {
                val statename = Jobject.getJSONObject("JH")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "JHARKAND$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))

                    if (total.length() > 3) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {

                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            28 -> {
                val statename = Jobject.getJSONObject("MN")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 2) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            29 -> {
                val statename = Jobject.getJSONObject("MZ")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "mizoram$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))

if(total.length()>1){
    val tested = total.getString("tested")
    lineentrytested.add(Entry(tested.toFloat(), i))
}
                    if (total.length() > 2) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {

                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            30 -> {
                val statename = Jobject.getJSONObject("AP")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))


                    if (total.length() > 1) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 3) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            31 -> {
                val statename = Jobject.getJSONObject("DN")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "dadra$total")

//
                    val tested = total.getString("tested")
                    lineentrytested.add(Entry(tested.toFloat(), i))

                    if (total.length() > 1) {
                                            val confirmed = total.getString("confirmed")
                        lineentryconfirmed.add(Entry(confirmed.toFloat(), i))
//                                   
//

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 2) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))
//
                    }
                    if (total.length() > 4) {
                        deceased = total.getString("deceased")
                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })
            }
            32 -> {
            val statename = Jobject.getJSONObject("DN")
            val dates = statename.getJSONObject("dates")
//                                                
//                                          
            val refineddates = dates.names()
//                                           
//                                         
            for (i in 0 until dates.length()) {
                //stores dates
                val key = refineddates.getString(i)
                xvalue.add(key)
                val refine = dates.getJSONObject(key)


                val total = refine.getJSONObject("total")
                Log.d("TAG", "dadra$total")

//
                val tested = total.getString("tested")
                lineentrytested.add(Entry(tested.toFloat(), i))

                if (total.length() > 1) {
                    val confirmed = total.getString("confirmed")
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))
//                                   
//

//                                        Log.d("TAG","${recovered.length}")
                }
                if (total.length() > 2) {
                    recovered = total.getString("recovered")
                    lineentry.add(Entry(recovered.toFloat(), i))
//
                }
                if (total.length() > 4) {
                    deceased = total.getString("deceased")
                    lineentrydeceased.add(Entry(deceased.toFloat(), i))
                    val vaccinated = total.remove("vaccinated")
                }
            }
            runOnUiThread(kotlinx.coroutines.Runnable {
                val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                val linedataset = LineDataSet(lineentry, "Total Recovered")
                val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                linedataset.circleRadius = 0F
                linedatasettested.circleRadius = 0F
                linedatasettested.color = Color.BLACK
                linedatasettested.setDrawFilled(true)
                linedatasettested.fillColor = Color.BLACK
                linedatasetconfirmed.circleRadius = 0F
                linedatasetconfirmed.color = Color.BLUE
                linedatasetconfirmed.setDrawFilled(true)
                linedatasetconfirmed.fillColor = Color.BLUE
                
                
                
                linedataset.color = greencolor
                linedataset.setDrawFilled(true)
                linedataset.fillColor = greencolor
                runOnUiThread(kotlinx.coroutines.Runnable {
                    finaldataset.add(linedatasetconfirmed)
                    finaldataset.add(linedataset)
                })
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                    val data = LineData(xvalue, linedataset)
                    val datafirst = LineData(xvalue, linedatasettested)


                    lineChart2.data = datasecondgraph
                    lineChart.data = datafirst

                })
            })

            runOnUiThread(Runnable {
                lineChart2.animateXY(3000, 3000)
                lineChart.animateXY(3000, 3000)
            })
        }
            33 -> { val statename = Jobject.getJSONObject("LD")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "laks$total")

//


                    if (total.length() > 1) {
                        val confirmed = total.getString("confirmed")
                        lineentryconfirmed.add(Entry(confirmed.toFloat(), i))

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 2) {
//
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 3) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))
                                            val confirmed = total.getString("confirmed")
                                   
//                        deceased = total.getString("deceased")
//                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })}
            34 -> {val statename = Jobject.getJSONObject("ML")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "ML$total")
                    val confirmed = total.getString("confirmed")
//                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))



                    if (total.length() > 2) {
                        val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    }
                    if (total.length() > 3) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))
//                        deceased = total.getString("deceased")
//                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })}
            35 -> {  val statename = Jobject.getJSONObject("NL")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "NAG$total")
                    val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
//


                    if (total.length() > 1) {
                        val confirmed = total.getString("confirmed")
////                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))
//

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 2) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))
//
                    }
                    if (total.length() > 4) {
//                        deceased = total.getString("deceased")
//                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })}
            36 -> { val statename = Jobject.getJSONObject("SK")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "sikkim$total")
//

                    val tested = total.getString("tested")
                        lineentrytested.add(Entry(tested.toFloat(), i))
                    if (total.length() > 1) {
                        val confirmed = total.getString("confirmed")
//////                                   
                    lineentryconfirmed.add(Entry(confirmed.toFloat(), i))
//

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 2) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))
////
                    }
                    if (total.length() > 4) {
//                        deceased = total.getString("deceased")
//                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    
                    
                    
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })}
            37 -> { val statename = Jobject.getJSONObject("TR")
                val dates = statename.getJSONObject("dates")
//                                                
//                                          
                val refineddates = dates.names()
//                                           
//                                         
                for (i in 0 until dates.length()) {
                    //stores dates
                    val key = refineddates.getString(i)
                    xvalue.add(key)
                    val refine = dates.getJSONObject(key)


                    val total = refine.getJSONObject("total")
                    Log.d("TAG", "sikkim$total")
//

//
                    if (total.length() > 1) {
                        val tested = total.getString("tested")
                    lineentrytested.add(Entry(tested.toFloat(), i))
                        val confirmed = total.getString("confirmed")
//////                                   
                        lineentryconfirmed.add(Entry(confirmed.toFloat(), i))
//

//                                        Log.d("TAG","${recovered.length}")
                    }
                    if (total.length() > 2) {
                        recovered = total.getString("recovered")
                        lineentry.add(Entry(recovered.toFloat(), i))
////
                    }
                    if (total.length() > 4) {
//                        deceased = total.getString("deceased")
//                        lineentrydeceased.add(Entry(deceased.toFloat(), i))
                        val vaccinated = total.remove("vaccinated")
                    }
                }
                runOnUiThread(kotlinx.coroutines.Runnable {
                    val greencolor = ContextCompat.getColor(applicationContext, R.color.green)
                    val linedataset = LineDataSet(lineentry, "Total Recovered")
                    val linedatasetconfirmed = LineDataSet(lineentryconfirmed, "Total Confirmed")
                    val linedatasetdeceased = LineDataSet(lineentrydeceased, "Total Deaths")
                    val linedatasettested = LineDataSet(lineentrytested, "Total Tested")


                    linedataset.circleRadius = 0F
                    linedatasettested.circleRadius = 0F
                    linedatasettested.color = Color.BLACK
                    linedatasettested.setDrawFilled(true)
                    linedatasettested.fillColor = Color.BLACK
                    linedatasetconfirmed.circleRadius = 0F
                    linedatasetconfirmed.color = Color.BLUE
                    linedatasetconfirmed.setDrawFilled(true)
                    linedatasetconfirmed.fillColor = Color.BLUE
                    linedataset.color = greencolor
                    linedataset.setDrawFilled(true)
                    linedataset.fillColor = greencolor
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        finaldataset.add(linedatasetconfirmed)
                        finaldataset.add(linedataset)
                    })
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        val datasecondgraph = LineData(xvalue, finaldataset as List<ILineDataSet>?)

                        val data = LineData(xvalue, linedataset)
                        val datafirst = LineData(xvalue, linedatasettested)


                        lineChart2.data = datasecondgraph
                        lineChart.data = datafirst

                    })
                })

                runOnUiThread(Runnable {
                    lineChart2.animateXY(3000, 3000)
                    lineChart.animateXY(3000, 3000)
                })}

        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
    fun getAlertDialog(
            context: Context,
            layout: Int,
            setCancellationOnTouchOutside: Boolean
    ): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val customLayout: View =
                layoutInflater.inflate(layout, null)
        builder.setView(customLayout)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(setCancellationOnTouchOutside)
        return dialog
    }
    fun linechartproperties(finalbluecolor:Int,bluecolor:Int,greencolor:Int,font:Typeface,blackcolor:Int){
        lineChart.setDescription("Dates")
        lineChart.setDescriptionTypeface(font)
        lineChart.legend.typeface = font
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.setBorderColor(finalbluecolor)
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.axisRight.setDrawGridLines(false)
        lineChart.axisLeft.textColor = bluecolor
        lineChart.axisRight.textColor = bluecolor
        lineChart.xAxis.textColor = blackcolor
        lineChart.xAxis.typeface = font
        lineChart.axisLeft.setDrawAxisLine(false)
        lineChart.axisRight.axisLineColor = bluecolor
        lineChart.xAxis.axisLineColor = bluecolor
        lineChart.xAxis.axisLineColor = bluecolor
        lineChart.axisRight.axisLineWidth = 3F
        lineChart.xAxis.axisLineWidth = 3F
        lineChart.axisLeft.typeface= font
        lineChart.axisRight.typeface= font
        lineChart2.legend.typeface = font
        lineChart2.setDescription("Dates")
        lineChart2.setDescriptionTypeface(font)
        lineChart2.axisRight.axisLineColor = greencolor
        lineChart2.xAxis.axisLineColor = greencolor
        lineChart2.axisRight.axisLineWidth = 3F
        lineChart2.xAxis.axisLineWidth = 3F
        lineChart2.xAxis.setDrawGridLines(false)
        lineChart2.axisLeft.setDrawGridLines(false)
        lineChart2.axisRight.setDrawGridLines(false)
        lineChart2.axisLeft.setDrawAxisLine(false)
        lineChart2.axisLeft.textColor = greencolor
        lineChart2.axisRight.textColor = greencolor
        lineChart2.xAxis.textColor = blackcolor
        lineChart2.xAxis.typeface = font
        lineChart2.axisLeft.typeface= font
        lineChart2.axisRight.typeface= font
    } }



