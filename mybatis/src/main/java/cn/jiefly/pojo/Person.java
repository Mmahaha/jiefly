package cn.jiefly.pojo;

public class Person {
    private long fid;
    private String fname;
    private String fsex;

    public Person(long fid, String fname, String fsex) {
        this.fid = fid;
        this.fname = fname;
        this.fsex = fsex;
    }

    public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFsex() {
        return fsex;
    }

    public void setFsex(String fsex) {
        this.fsex = fsex;
    }

    @Override
    public String toString() {
        return "Person{" +
                "fid=" + fid +
                 ", fname='" + fname + '\'' +
                ", fsex='" + fsex + '\'' +
                '}';
    }
}
