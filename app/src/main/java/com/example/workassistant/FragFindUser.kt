package com.example.workassistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import java.net.URL

class FragFindUser: Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_select_user, container, false)
        return rootView
    }

/*
    private fun fillUsersInRole(fkey: String): List<MyUser> {
        val res = URL(apiURL + "/roles/role_users/?fkey=" + fkey).getText(token_type,access_token)
        return Gson().fromJson(res, Array<MyUser>::class.java).asList()
    }
*/

}