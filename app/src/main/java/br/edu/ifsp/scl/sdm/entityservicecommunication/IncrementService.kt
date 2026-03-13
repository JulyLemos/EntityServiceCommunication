package br.edu.ifsp.scl.sdm.entityservicecommunication

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message

class IncrementService : Service() {

    private inner class IncrementHandler(looper: Looper): Handler(looper){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            msg.data.getInt("VALUE").also {
//                InterEntityCommunication.valueLiveData.postValue(it + 1) //livedata que continua a ser observado no main activity
                Intent("INCREMENT_VALUE_ACTION").putExtra("VALUE", it + 1).apply {
                    sendBroadcast(this)
                }
            } //o trabalho do serviço vai ser executado numa thread separada
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getIntExtra("VALUE", -1)?.also { value ->
            HandlerThread("IncrementThread").apply { //vai pegar a mensagem e o valor que veio dentro dessa mensagem
                start()
                IncrementHandler(looper).apply {
                    //não é adequado ficar criando objetos do tipo message
                    obtainMessage().apply {
                        data.putInt("VALUE", value)
                        sendMessage(this)
                    }
                }
            }
        }
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent): IBinder? = null
}