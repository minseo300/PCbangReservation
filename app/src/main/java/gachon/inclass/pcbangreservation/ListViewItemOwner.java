package gachon.inclass.pcbangreservation;

public class ListViewItemOwner {
    private String seat_num ;

    private String time;

    ListViewItemOwner(String name,String t)
    {
        this.seat_num=name;
        this.time=t;
    }

    public void setText(String name,String t) {

        this.seat_num = name ;
        this.time=t;
    }

    public String getSeat_num() {
        return this.seat_num;
    }
    public String getTime(){return this.time;}

}
