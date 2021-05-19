package gachon.inclass.pcbangreservation;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    private String store_name ;
    private String detail;
    private String address;

    ListViewItem(String name,String add,String detail)
    {
        this.store_name=name;
        this.address=add;
        this.detail=detail;
    }

    public void setText(String name,String add, String details) {

        this.store_name = name ;
        this.address=add;
        this.detail=details;
    }

    public String getStore_name() {
        return this.store_name ;
    }
    public String getAddress(){return this.address;}
    public String getDetail(){return this.detail;}

}
