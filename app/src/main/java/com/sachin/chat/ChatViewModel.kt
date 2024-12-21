package com.sachin.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<ModelActivity>()
    }

    val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = Data.apiKey
    )

    fun sendMessage(question : String){
        viewModelScope.launch {

            try{
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role){ text(it.message) }
                    }.toList()
                )

                messageList.add(ModelActivity(question,"user"))
                messageList.add(ModelActivity("Waiting....","model"))

                val response = chat.sendMessage(question)
                messageList.removeLast()
                messageList.add(ModelActivity(response.text.toString(),"model"))
            }catch (e : Exception){
                messageList.removeLast()
                messageList.add(ModelActivity("Error : "+e.message.toString(),"model"))
            }


        }
    }
}