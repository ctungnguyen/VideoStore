public class Item {
    private String id;
    private String title;
    private String loanType;
    private int leftCopy;
    private Double rentalFee;
    private String rentalStatus;

    public Item(String id, String title, String loanType, int leftCopy, Double rentalFee){
        this.id = id;
        this.title = title;
        this.loanType = loanType;
        this.leftCopy = leftCopy;
        this.rentalFee = rentalFee;
        if (this.leftCopy != 0){
            this.rentalStatus = "Available";
        }else this.rentalStatus = "Borrowed";
    }
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return loanType;
    }

    public int getLeftCopy() {
        return leftCopy;
    }

    public double getRentalFee() {
        return rentalFee;
    }

    public String getRentalStatus() {
        return rentalStatus;
    }

    public void getItemInfo(){
        System.out.println(this.getId() + " " + this.getTitle() + " " + this.getLeftCopy() + this.getType() + " " + this.getRentalFee() + " " + this.getRentalStatus() );
    }

    public void setTitle(String t) { this.title = t; }

    public void setLeftCopy(int t) {
        this.leftCopy = t;
        if (this.leftCopy != 0){
            this.rentalStatus = "Available";
        }else this.rentalStatus = "Borrowed";
    }
    public void setRentalFee(double t) { this.rentalFee = t; }

}
