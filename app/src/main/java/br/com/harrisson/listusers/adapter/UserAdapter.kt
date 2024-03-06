package br.com.harrisson.listusers.adapter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.recyclerview.widget.RecyclerView
import br.com.harrisson.listusers.R
import br.com.harrisson.listusers.User
import br.com.harrisson.listusers.databinding.ItemUserBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class UserAdapter(val context: Context, private val listUsers: MutableList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        private var db =  Firebase.firestore
    val user = Firebase.auth.currentUser!!




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemUser = ItemUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserViewHolder(itemUser)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.id.text = listUsers[position].id
        holder.name.text = listUsers[position].name
        holder.email.text = listUsers[position].email

        holder.editButton.setOnClickListener { v: View ->
            showMenu(v, R.menu.edit_user_menu, holder.name.text.toString(), holder.id.text.toString() )
        }


    }

    override fun getItemCount(): Int = listUsers.size

    inner class UserViewHolder(binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.nameUser
        val email = binding.emailUser
        val id = binding.idUser
        val editButton = binding.btnUserMenu
    }

    @SuppressLint("RestrictedApi")
    private fun showMenu(v: View, @MenuRes menuRes: Int, name: String, id: String) {
        val popup = PopupMenu(context!!, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        if (popup.menu is MenuBuilder) {
            val menuBuilder = popup.menu as MenuBuilder
            menuBuilder.setOptionalIconsVisible(true)
            for (item in menuBuilder.visibleItems) {
                val iconMarginPx =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, Float.MAX_VALUE , DisplayMetrics()
                    )
                        .toInt()
                if (item.icon != null) {
                    item.icon = InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx,0)
                }

            }
        }
        popup.show()

        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.edit_selected -> {
                    Toast.makeText(
                        context,
                        "Editando $name" ,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnMenuItemClickListener true
                }
                R.id.delete_selected -> {
                    //delete selected item
                    db = FirebaseFirestore.getInstance()

                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "Usuário Apagado.")
                            }
                        }

                    db.collection("users")
                        .document(id)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Usuário $name\n removido com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener{
                            Toast.makeText(
                                context,
                                "Usuário $name\n não foi removido",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    return@setOnMenuItemClickListener true
                }
            }
            false
        }


    }

    fun atualizaListaUser() {
        db.collection("users")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }
                if (value != null) {
                    listUsers.clear()
                    for (document in value) {
                        val id = document.id
                        val name = document.getString("name")
                        val email = document.getString("email")
                        val user = User(id, name, email)
                        listUsers.add(user)
                    }
                    notifyDataSetChanged()
                }
            }

        Toast.makeText(context, "Lista atualizada", Toast.LENGTH_SHORT).show()
    }




}

