package com.ezyshopproject.ezycarts.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ezyshopproject.ezycarts.R;
import com.ezyshopproject.ezycarts.databinding.ItemCartBinding;
import com.ezyshopproject.ezycarts.databinding.QuantityDailogBinding;
import com.ezyshopproject.ezycarts.models.Product;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {


    Context context ;
    ArrayList<Product> products ;
    CartListener cartListener ;
    Cart cart ;

    public interface CartListener{
        public void  onQuantityChanged();
    }

    public CartAdapter(Context context, ArrayList<Product> products,CartListener cartListener) {
        this.context = context;
        this.products = products;
        this.cartListener= cartListener;
         cart = TinyCartHelper.getCart();
    }




    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = products.get(position);

        Glide.with(context)
                .load(product.getImage())
                .into(holder.binding.image);

        holder.binding.name.setText(product.getName());
        holder.binding.price.setText("₹"+product.getPrice());
        holder.binding.quantity.setText(product.getQuantity()+"item(S)");

        holder.itemView.setOnClickListener(v -> {
            QuantityDailogBinding quantityDailogBinding = QuantityDailogBinding.inflate(LayoutInflater.from(context));

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(quantityDailogBinding.getRoot())
                    .create();


            quantityDailogBinding.productName.setText(product.getName());
            quantityDailogBinding.productStock.setText("Stock"+product.getStock());
            quantityDailogBinding.quantity.setText( String.valueOf(product.getQuantity()));

            int stock = product.getStock();


            quantityDailogBinding.plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int quantity = product.getQuantity();
                    quantity++;


                    if (quantity>product.getStock()){
                        Toast.makeText(context, "Max stock available : "+product.getStock(), Toast.LENGTH_SHORT).show();

                    }else {
                        product.setQuantity(quantity);
                        quantityDailogBinding.quantity.setText(String.valueOf(quantity));
                    }

                    notifyDataSetChanged();
                    cart.updateItem(product,product.getQuantity());
                    cartListener.onQuantityChanged();

                }
            });

            quantityDailogBinding.minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int quantity = product.getQuantity();
                    if (quantity > 1 )
                        quantity--;
                    product.setQuantity(quantity);
                    quantityDailogBinding.quantity.setText(String.valueOf(quantity));

                    notifyDataSetChanged();
                    cart.updateItem(product,product.getQuantity());
                    cartListener.onQuantityChanged();
                }
            });

            quantityDailogBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Toast.makeText(context, "Item Save", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.show();

        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder{

         ItemCartBinding binding ;
         public CartViewHolder(@NonNull View itemView) {
             super(itemView);

             binding= ItemCartBinding.bind(itemView);

         }
     }
}
