package com.esri.arcgisruntime.demos.displaymap;

import android.os.Environment;

import java.io.File;

public class FileManage {
    private String RootPath;
    private String LastPath;
    private String FileType;
    private String[] FileSubset;
    private File file;
    public static int BUBBLESORT = -1;
    public static int MERGERSORT = -2;

    public void setFileType(String fileType) {
        FileType = fileType;
    }

    public FileManage(String fileType) {
        RootPath = Environment.getExternalStorageDirectory().toString();
        LastPath = Environment.getExternalStorageDirectory().toString();
        FileType = fileType;
        file = new File(RootPath);
        if (FileType.equals("")) FileSubset = file.list();
        else {
            String[] strings = file.list();
            int num = 0;
            for (int i = 0; i < strings.length; i++){
                if (strings[i].contains(FileType)) {
                    num++;
                }else {
                    File f = new File(RootPath + "/" + strings[i]);
                    if (f.isDirectory() & !strings[i].substring(0, 1).equals(".")) num++;
                }
            }
            FileSubset = new String[num];
            for (int i = 0, j = 0; i < strings.length; i++){
                if (strings[i].contains(FileType)){
                    FileSubset[j] = strings[i];
                    j++;
                }else {
                    File f = new File(RootPath + "/" + strings[i]);
                    if (f.isDirectory() & !strings[i].substring(0, 1).equals(".")) {
                        FileSubset[j] = strings[i];
                        j++;
                    }
                }
            }
        }
    }

    public FileManage(String rootPath, String lastPath, String fileType) {
        RootPath = rootPath;
        LastPath = lastPath;
        FileType = fileType;
        file = new File(RootPath);
        if (FileType.equals("")) FileSubset = file.list();
        else {
            String[] strings = file.list();
            int num = 0;
            for (int i = 0; i < strings.length; i++){
                if (strings[i].contains(FileType)) {
                    num++;
                }else {
                    File f = new File(RootPath + "/" + strings[i]);
                    if (f.isDirectory() & !strings[i].substring(0, 1).equals(".")) num++;
                }
            }
            FileSubset = new String[num];
            for (int i = 0, j = 0; i < strings.length; i++){
                if (strings[i].contains(FileType)){
                    FileSubset[j] = strings[i];
                    j++;
                }else {
                    File f = new File(RootPath + "/" + strings[i]);
                    if (f.isDirectory() & !strings[i].substring(0, 1).equals(".")) {
                        FileSubset[j] = strings[i];
                        j++;
                    }
                }
            }
        }
    }

    public FileManage(String rootPath, String lastPath, int Type) {
        RootPath = rootPath;
        LastPath = lastPath;
    }

    public FileManage SelectLast(){
        if (!RootPath.equals(Environment.getExternalStorageDirectory().toString())){
            RootPath = LastPath;
            LastPath = LastPath.substring(0, LastPath.lastIndexOf("/"));
            file = new File(RootPath);
            FileSubset = file.list();
            if (FileType.equals("")) FileSubset = file.list();
            else {
                String[] strings = file.list();
                int num = 0;
                for (int i = 0; i < strings.length; i++){
                    if (strings[i].contains(FileType)) {
                        num++;
                    }else {
                        File f = new File(RootPath + "/" + strings[i]);
                        if (f.isDirectory() & !strings[i].substring(0, 1).equals(".")) num++;
                    }
                }
                FileSubset = new String[num];
                for (int i = 0, j = 0; i < strings.length; i++){
                    if (strings[i].contains(FileType)){
                        FileSubset[j] = strings[i];
                        j++;
                    }else {
                        File f = new File(RootPath + "/" + strings[i]);
                        if (f.isDirectory() & !strings[i].substring(0, 1).equals(".")) {
                            FileSubset[j] = strings[i];
                            j++;
                        }
                    }
                }
            }
        }
        return this;
    }

    public void SelectNext(FileManage fm){
        RootPath = fm.RootPath;
        LastPath = fm.LastPath;
        file = new File(RootPath);
        FileSubset = file.list();
    }

    public String getRootPath() {
        return RootPath;
    }

    public String[] getFileSubset() {
        return FileSubset;
    }

    public String[] getFileSubset(int type) {
        if (type == BUBBLESORT) return bubbleSort(FileSubset);
        else if (type == MERGERSORT) return MergerSort(FileSubset);
        else return FileSubset;
    }

    private static String[] MergerSort(String[] arr){
        String[]temp = new String[arr.length];//在排序前，先建好一个长度等于原数组长度的临时数组，避免递归中频繁开辟空间
        MergerSort(arr,0,arr.length-1,temp);
        return arr;
    }
    private static void MergerSort(String[] arr, int left, int right, String[]temp){
        if(left<right){
            int mid = (left+right)/2;
            MergerSort(arr,left,mid,temp);//左边归并排序，使得左子序列有序
            MergerSort(arr,mid+1,right,temp);//右边归并排序，使得右子序列有序
            MergerMerge(arr,left,mid,right,temp);//将两个有序子数组合并操作
        }
    }
    private static void MergerMerge(String[] arr, int left, int mid, int right, String[] temp){
        int i = left;//左序列指针
        int j = mid+1;//右序列指针
        int t = 0;//临时数组指针
        while (i<=mid && j<=right){
            if(arr[i].toUpperCase().charAt(0)<=arr[j].toUpperCase().charAt(0)){
                temp[t++] = arr[i++];
            }else {
                temp[t++] = arr[j++];
            }
        }
        while(i<=mid){//将左边剩余元素填充进temp中
            temp[t++] = arr[i++];
        }
        while(j<=right){//将右序列剩余元素填充进temp中
            temp[t++] = arr[j++];
        }
        t = 0;
        //将temp中的元素全部拷贝到原数组中
        while(left <= right){
            arr[left++] = temp[t++];
        }
    }

    private String[] bubbleSort(String[] arr) {
        int len = arr.length;
        for (int i = 0; i < len - 1; i++) {
            for (int j = 0; j < len - 1 - i; j++) {
                if (arr[j].toUpperCase().charAt(0) > arr[j + 1].toUpperCase().charAt(0)) {        // 相邻元素两两对比
                    String temp = arr[j+1];        // 元素交换
                    arr[j+1] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        return arr;
    }
}
