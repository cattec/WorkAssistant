package com.example.workassistant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.workassistant.databinding.ActivityChannelBinding
import com.getstream.sdk.chat.view.MessageListView
import com.getstream.sdk.chat.viewmodel.ChannelHeaderViewModel
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.bindView
import com.getstream.sdk.chat.viewmodel.factory.ChannelViewModelFactory
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Normal
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Thread
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.State.NavigateUp
//import com.getstream.sdk.chat.viewmodel.messages.bindView
import io.getstream.chat.android.client.models.Channel

class ChannelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChannelBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        try {
            super.onCreate(savedInstanceState)

            binding = ActivityChannelBinding.inflate(layoutInflater)
            setContentView(binding.root)
            //setContentView(R.layout.activity_channel)

            val cid = checkNotNull(intent.getStringExtra(CID_KEY)) {
                "Specifying a channel id is required when starting ChannelActivity"
            }

            // Step 1 - Create 3 separate ViewModels for the views so it's easy to customize one of the components
            val factory = ChannelViewModelFactory(cid)
            val channelHeaderViewModel: ChannelHeaderViewModel by viewModels { factory }
            val messageListViewModel: MessageListViewModel by viewModels { factory }
            val messageInputViewModel: MessageInputViewModel by viewModels { factory }

            // TODO set custom AttachmentViewHolderFactory

            // Step 2 - Bind the view and ViewModels, they are loosely coupled so it's easy to customize
            channelHeaderViewModel.bindView(findViewById(R.id.channelHeaderView),this)
            //messageListViewModel.bindView(findViewById(R.id.messageListView),this)
            messageInputViewModel.bindView(findViewById(R.id.messageInputView),this)

            // Step 3 - Let the message input know when we open a thread
            // Note: support for listening to LiveData like this was added in kotlin 1.4, upgrade kotlin if you run into issues
            messageListViewModel.mode.observe(this) { mode ->
                when (mode) {
                    is Thread -> messageInputViewModel.setActiveThread(mode.parentMessage)
                    is MessageListViewModel.Mode.Normal -> messageInputViewModel.resetThread()
                }
            }

            // Step 4 - Let the message input know when we are editing a message
            findViewById<MessageListView>(R.id.messageListView).setOnMessageEditHandler {
                messageInputViewModel.editMessage.postValue(it)
            }

            // Step 5 - Handle navigate up state
            messageListViewModel.state.observe(this) { state ->
                if (state is MessageListViewModel.State.NavigateUp) {
                    finish()
                }
            }

            // Step 6 - Handle back button behaviour correctly when you're in a thread
            binding.channelHeaderView.onBackClick = {
                messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
            }

            onBackPressedDispatcher.addCallback(this) {
                binding.channelHeaderView.onBackClick()
            }

        }
        catch (e: Exception)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val CID_KEY = "key:cid"

        fun newIntent(context: Context, channel: Channel): Intent =
            Intent(context, ChannelActivity::class.java).putExtra(CID_KEY, channel.cid)
    }
}