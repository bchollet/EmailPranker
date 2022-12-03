public enum StatusCodes {
    READY("220"),
    OK("250"),
    BYE("221"),
    DATA("354");

    public final String value;
    StatusCodes(String value) {
        this.value = value;
    }
}
