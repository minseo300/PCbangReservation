package gachon.inclass.pcbangreservation;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    private String store_name ;

    private String address;

    ListViewItem(String name,String add)
    {
        this.store_name=name;
        this.address=add;
    }

    public void setText(String name,String add) {

        this.store_name = name ;
        this.address=add;
    }

    public String getStore_name() {
        return this.store_name ;
    }
    public String getAddress(){return this.address;}

}
