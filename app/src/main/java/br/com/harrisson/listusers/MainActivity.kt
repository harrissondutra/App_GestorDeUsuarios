package br.com.harrisson.listusers

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import br.com.harrisson.listusers.adapter.UserAdapter
import br.com.harrisson.listusers.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var listUsers: MutableList<User> = mutableListOf()
    private var db = FirebaseFirestore.getInstance()





    @SuppressLint("ResourceType", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)





        val recyclerView = binding.recyclerView
        val contUser = binding.countUsers


        db.collection("users").get()
            .addOnSuccessListener {
                for (document in it) {
                    val id = document.id
                    val name = document.getString("name")
                    val email = document.getString("email")
                    val user = User(id, name, email)
                    listUsers.add(user)
                }
                val adapter = UserAdapter(this, listUsers)
                binding.recyclerView.adapter = adapter
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = LinearLayoutManager(this)
                contUser.text = adapter.itemCount.toString()
                contUser.setTextColor(Color.BLACK)
                contUser.setTypeface(null, Typeface.BOLD)
                adapter.notifyDataSetChanged()


                val swipeContainer = findViewById<SwipeRefreshLayout>(R.id.swipeContainer)
                swipeContainer.setOnRefreshListener {
                    adapter.atualizaListaUser()
                    swipeContainer.isRefreshing = false
                }
                swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light)
                swipeContainer.post {
                    swipeContainer.isRefreshing = false
                }

            }

        


    }

}