package br.edu.ifsp.scl.sdm.entityservicecommunication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.scl.sdm.entityservicecommunication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var incrementServiceIntent: Intent
    private var counter = 0
    private val incrementBroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            intent?.getIntExtra("VALUE", -1)?.also { value ->
                counter = value
                Toast.makeText(this@MainActivity, "You clicked $counter times.", Toast.LENGTH_SHORT).show()

            }
        }
    } //broadcast receiver é bom que receba mensagem apenas quando tem interação com o usuário

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        incrementServiceIntent = Intent(this, IncrementService::class.java)
        with (amb) {
            mainTb.apply {
                getString(R.string.app_name).also { setTitle(it) }
                setSupportActionBar(this)
            }
            incrementBt.setOnClickListener {
                startService(incrementServiceIntent.apply {
                    putExtra("VALUE", counter)
                })
            }
        }
//        InterEntityCommunication.valueLiveData.observe(this) { value ->
//            counter = value
//            Toast.makeText(this, "You clicked $counter times.", Toast.LENGTH_SHORT).show()
//        } -> não é mais necessária, já que possuímos o broadcast receiver
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU) //coloquei isso, pois estava dando erro no register
    override fun onStart() {
        super.onStart()
        registerReceiver(incrementBroadcastReceiver, IntentFilter("INCREMENT_VALUE_ACTION"), RECEIVER_NOT_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(incrementBroadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(incrementServiceIntent)
    }
}