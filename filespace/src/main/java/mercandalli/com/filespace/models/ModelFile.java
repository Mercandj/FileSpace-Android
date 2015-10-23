/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.models;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Constants;
import mercandalli.com.filespace.listeners.IBitmapListener;
import mercandalli.com.filespace.listeners.IListener;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.net.TaskGetDownload;
import mercandalli.com.filespace.net.TaskGetDownloadImage;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activities.ApplicationCallback;
import mercandalli.com.filespace.ui.activities.FileAudioActivity;
import mercandalli.com.filespace.ui.activities.FilePictureActivity;
import mercandalli.com.filespace.ui.activities.FileTextActivity;
import mercandalli.com.filespace.ui.activities.FileTimerActivity;
import mercandalli.com.filespace.utils.FileUtils;
import mercandalli.com.filespace.utils.HtmlUtils;
import mercandalli.com.filespace.utils.ImageUtils;
import mercandalli.com.filespace.utils.StringPair;
import mercandalli.com.filespace.utils.StringUtils;
import mercandalli.com.filespace.utils.TimeUtils;

public class ModelFile extends Model implements Parcelable {

    public int id, id_user;
    public int id_file_parent = -1;
    public String url;
    public String name;
    public long size;
    public ModelFileType type;
    public boolean directory = false;
    public boolean _public = false;
    public boolean is_apk_update = false;
    private long mLastModified;
    public Date date_creation;
    public Bitmap bitmap;
    private File file;
    public String onlineUrl;
    public ModelFileSpace content;
    public boolean selected = false;
    public int count;

    public String adapterTitleStart = "";

    public String getAdapterTitle() {
        if (this.type == null) {
            if (this.name != null)
                return this.adapterTitleStart + this.getNameExt();
            else
                return this.adapterTitleStart + this.url;
        } else if (this.type.equals(ModelFileTypeENUM.FILESPACE.type) && this.content != null)
            return this.adapterTitleStart + this.content.getAdapterTitle();
        else if (this.name != null)
            return this.adapterTitleStart + this.getNameExt();
        else
            return this.adapterTitleStart + this.url;
    }

    public String getAdapterSubtitle() {
        if (this.directory && this.count != 0)
            return "Directory: " + StringUtils.intToShortString(this.count) + " file" + (this.count > 1 ? "s" : "");
        if (this.directory)
            return "Directory";
        if (ModelFileTypeENUM.FILESPACE.type.equals(this.type) && this.content != null)
            return type.getTitle() + " " + StringUtils.capitalize(this.content.type.type.toString());
        if (type != null)
            return type.getTitle();
        return "";
    }

    public String getNameExt() {
        return this.name + ((this.directory) ? "" : ("." + this.type));
    }

    public String getNameExtCopy() {
        return this.name + " - Copy" + ((this.directory) ? "" : ("." + this.type));
    }

    public List<StringPair> getForUpload() {
        List<StringPair> parameters = new ArrayList<>();
        if (name != null)
            parameters.add(new StringPair("url", this.name));
        if (directory)
            parameters.add(new StringPair("directory", "true"));
        if (id_file_parent != -1)
            parameters.add(new StringPair("id_file_parent", "" + this.id_file_parent));
        return parameters;
    }

    public List<StringPair> getForRename() {
        List<StringPair> parameters = new ArrayList<>();
        if (name != null)
            parameters.add(new StringPair("url", this.name));
        if (directory)
            parameters.add(new StringPair("directory", "true"));
        return parameters;
    }

    public ModelFile(Activity activity, ApplicationCallback app) {
        super(activity, app);
    }

    public ModelFile(Activity activity, ApplicationCallback app, File file) {
        super(activity, app);
        setFile(file);
    }

    public ModelFile(Activity activity, ApplicationCallback app, JSONObject json) {
        super(activity, app);

        try {
            if (json.has("id") && !json.isNull("id")) {
                this.id = json.getInt("id");
                this.onlineUrl = this.app.getConfig().getUrlServer() + this.app.getConfig().routeFile + "/" + id;
            }
            if (json.has("id_user") && !json.isNull("id_user")) {
                this.id_user = json.getInt("id_user");
            }
            if (json.has("id_file_parent") && !json.isNull("id_file_parent")) {
                this.id_file_parent = json.getInt("id_file_parent");
            }
            if (json.has("url"))
                this.url = json.getString("url");
            if (json.has("name"))
                this.name = json.getString("name");
            if (json.has("type"))
                this.type = new ModelFileType(json.getString("type"));
            if (json.has("size") && !json.isNull("size"))
                this.size = json.getLong("size");
            if (json.has("count") && !json.isNull("count"))
                this.count = json.getInt("count");
            if (json.has("directory") && !json.isNull("directory"))
                this.directory = json.getInt("directory") == 1;
            if (json.has("content") && !json.isNull("content"))
                this.content = new ModelFileSpace(mActivity, app, new JSONObject(json.getString("content")));
            if (json.has("public") && !json.isNull("public"))
                this._public = json.getInt("public") == 1;
            if (json.has("is_apk_update") && !json.isNull("is_apk_update"))
                this.is_apk_update = json.getInt("is_apk_update") == 1;
            if (json.has("date_creation") && !json.isNull("date_creation")) {
                SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
                this.date_creation = dateFormatGmt.parse(json.getString("date_creation"));
            }

        } catch (JSONException e) {
            Log.e("model ModelFile", "JSONException");
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (this.type.equals(ModelFileTypeENUM.PICTURE.type) && this.size >= 0) {
            if (ImageUtils.is_image(mActivity, this.id)) {
                ModelFile.this.bitmap = ImageUtils.load_image(mActivity, this.id);
                ModelFile.this.app.updateAdapters();
            } else
                new TaskGetDownloadImage(mActivity, app, this.app.getConfig().getUser(), this, Constants.SIZE_MAX_ONLINE_PICTURE_ICON, new IBitmapListener() {
                    @Override
                    public void execute(Bitmap bitmap) {
                        if (bitmap != null) {
                            ModelFile.this.bitmap = bitmap;
                            ModelFile.this.app.updateAdapters();
                        }
                    }
                }).execute();
        }
    }

    public void executeOnline(ArrayList<ModelFile> files, View view) {
        if (this.type.equals(ModelFileTypeENUM.TEXT.type)) {
            FileTextActivity.startForSelection(mActivity, this, true);
        } else if (this.type.equals(ModelFileTypeENUM.PICTURE.type)) {
            Intent intent = new Intent(mActivity, FilePictureActivity.class);
            intent.putExtra("ID", this.id);
            intent.putExtra("TITLE", "" + this.getNameExt());
            intent.putExtra("URL_FILE", "" + this.onlineUrl);
            intent.putExtra("LOGIN", "" + this.app.getConfig().getUser().getAccessLogin());
            intent.putExtra("PASSWORD", "" + this.app.getConfig().getUser().getAccessPassword());
            intent.putExtra("ONLINE", true);
            intent.putExtra("SIZE_FILE", size);
            intent.putExtra("DATE_FILE", date_creation);
            if (view == null) {
                this.mActivity.startActivity(intent);
                this.mActivity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else {
                Pair<View, String> p1 = Pair.create(view.findViewById(R.id.icon), "transitionIcon");
                Pair<View, String> p2 = Pair.create(view.findViewById(R.id.title), "transitionTitle");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(mActivity, p1, p2);
                this.mActivity.startActivity(intent, options.toBundle());
            }
        } else if (this.type.equals(ModelFileTypeENUM.AUDIO.type)) {
            Intent intent = new Intent(mActivity, FileAudioActivity.class);
            intent.putExtra("LOGIN", "" + app.getConfig().getUser().getAccessLogin());
            intent.putExtra("PASSWORD", "" + app.getConfig().getUser().getAccessPassword());
            intent.putExtra("ONLINE", true);
            intent.putExtra("FILE", this);
            ArrayList<ModelFile> tmpFiles = new ArrayList<>();
            for (ModelFile f : files)
                if (f.type.equals(ModelFileTypeENUM.AUDIO.type))
                    tmpFiles.add(f);
            intent.putParcelableArrayListExtra("FILES", tmpFiles);
            if (view == null) {
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else {
                Pair<View, String> p1 = Pair.create(view.findViewById(R.id.icon), "transitionIcon");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(mActivity, p1);
                mActivity.startActivity(intent, options.toBundle());
            }
        } else if (this.type.equals(ModelFileTypeENUM.FILESPACE.type)) {
            if (content != null) {
                if (content.timer.timer_date != null) {
                    Intent intent = new Intent(mActivity, FileTimerActivity.class);
                    intent.putExtra("URL_FILE", "" + this.onlineUrl);
                    intent.putExtra("LOGIN", "" + this.app.getConfig().getUser().getAccessLogin());
                    intent.putExtra("ONLINE", true);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    intent.putExtra("TIMER_DATE", "" + dateFormat.format(content.timer.timer_date));
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else if (!StringUtils.isNullOrEmpty(content.article.article_content_1)) {
                    FileTextActivity.startForSelection(mActivity, this, true);
                }
            }
        }
    }

    public void openLocalAs() {
        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
        String[] menuList = {
                mActivity.getString(R.string.text),
                mActivity.getString(R.string.image),
                mActivity.getString(R.string.audio),
                mActivity.getString(R.string.video),
                mActivity.getString(R.string.other)};
        menuAlert.setTitle(mActivity.getString(R.string.open_as));

        menuAlert.setItems(menuList,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        String type_mime = "";
                        switch (item) {
                            case 0:
                                type_mime = "text/plain";
                                break;
                            case 1:
                                type_mime = "image/*";
                                break;
                            case 2:
                                type_mime = "audio/*";
                                break;
                            case 3:
                                type_mime = "video/*";
                                break;
                        }
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_VIEW);
                        i.setDataAndType(Uri.fromFile(file), type_mime);
                        mActivity.startActivity(i);
                    }
                });
        AlertDialog menuDrop = menuAlert.create();
        menuDrop.show();
    }

    public void executeLocal(List<MusicModelFile> files, View view) {
        if (!file.exists())
            return;
        if (this.type.equals(ModelFileTypeENUM.APK.type)) {
            Intent apkIntent = new Intent();
            apkIntent.setAction(Intent.ACTION_VIEW);
            apkIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            mActivity.startActivity(apkIntent);
        } else if (this.type.equals(ModelFileTypeENUM.TEXT.type)) {
            Intent txtIntent = new Intent();
            txtIntent.setAction(Intent.ACTION_VIEW);
            txtIntent.setDataAndType(Uri.fromFile(file), "text/plain");
            try {
                mActivity.startActivity(txtIntent);
            } catch (ActivityNotFoundException e) {
                txtIntent.setType("text/*");
                mActivity.startActivity(txtIntent);
            }
        } else if (this.type.equals(ModelFileTypeENUM.HTML.type)) {
            Intent htmlIntent = new Intent();
            htmlIntent.setAction(Intent.ACTION_VIEW);
            htmlIntent.setDataAndType(Uri.fromFile(file), "text/html");
            try {
                this.mActivity.startActivity(htmlIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this.mActivity, "ERREUR", Toast.LENGTH_SHORT).show();
            }
        } else if (this.type.equals(ModelFileTypeENUM.AUDIO.type)) {
            Intent intent = new Intent(this.mActivity, FileAudioActivity.class);
            intent.putExtra("ONLINE", false);
            intent.putExtra("FILE", this);
            ArrayList<ModelFile> tmpFiles = new ArrayList<>();
            for (ModelFile f : files)
                if (f.type != null)
                    if (f.type.equals(ModelFileTypeENUM.AUDIO.type))
                        tmpFiles.add(f);
            intent.putParcelableArrayListExtra("FILES", tmpFiles);
            if (view == null) {
                this.mActivity.startActivity(intent);
                this.mActivity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else {
                Pair<View, String> p1 = Pair.create(view.findViewById(R.id.icon), "transitionIcon");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this.mActivity, p1);
                this.mActivity.startActivity(intent, options.toBundle());
            }
        } else if (this.type.equals(ModelFileTypeENUM.PICTURE.type)) {
            Intent picIntent = new Intent();
            picIntent.setAction(Intent.ACTION_VIEW);
            picIntent.setDataAndType(Uri.fromFile(file), "image/*");
            this.mActivity.startActivity(picIntent);
        } else if (this.type.equals(ModelFileTypeENUM.VIDEO.type)) {
            Intent videoIntent = new Intent();
            videoIntent.setAction(Intent.ACTION_VIEW);
            videoIntent.setDataAndType(Uri.fromFile(file), "video/*");
            try {
                mActivity.startActivity(videoIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mActivity, "ERREUR", Toast.LENGTH_SHORT).show();
            }
        } else if (this.type.equals(ModelFileTypeENUM.PDF.type)) {
            Intent pdfIntent = new Intent();
            pdfIntent.setAction(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
            try {
                mActivity.startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mActivity, "ERREUR", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void download(IListener listener) {
        if (this.directory) {
            Toast.makeText(mActivity, "Directory download not supported yet.", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = this.app.getConfig().getUrlServer() + this.app.getConfig().routeFile + "/" + id;
        String url_ouput = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + app.getConfig().getLocalFolderName() + File.separator + this.getNameExt();
        new TaskGetDownload(mActivity, this.app, url, url_ouput, this, listener).execute();
    }

    public boolean isOnline() {
        return (file == null);
    }

    public void delete(IPostExecuteListener listener) {
        if (this.isOnline()) {
            String url = this.app.getConfig().getUrlServer() + this.app.getConfig().routeFileDelete + "/" + id;
            new TaskPost(mActivity, app, url, listener).execute();
        } else {
            if (file.isDirectory())
                FileUtils.deleteDirectory(file);
            else
                file.delete();
            if (listener != null)
                listener.onPostExecute(null, null);
        }
    }

    public void setPublic(boolean public_, IPostExecuteListener listener) {
        this._public = public_;

        List<StringPair> parameters = new ArrayList<>();
        parameters.add(new StringPair("public", "" + this._public));
        String url = this.app.getConfig().getUrlServer() + this.app.getConfig().routeFile + "/" + this.id;
        (new TaskPost(mActivity, this.app, url, listener, parameters)).execute();
    }

    public void setId_file_parent(int id_file_parent, IPostExecuteListener listener) {
        this.id_file_parent = id_file_parent;

        List<StringPair> parameters = new ArrayList<>();
        parameters.add(new StringPair("id_file_parent", "" + this.id_file_parent));
        String url = this.app.getConfig().getUrlServer() + this.app.getConfig().routeFile + "/" + this.id;
        (new TaskPost(mActivity, this.app, url, listener, parameters)).execute();
    }

    public void rename(String new_name, IPostExecuteListener listener) {
        this.name = new_name;
        if (isOnline()) {
            this.url = new_name;
            String url = this.app.getConfig().getUrlServer() + this.app.getConfig().routeFile + "/" + id;
            new TaskPost(mActivity, app, url, listener, getForRename()).execute();
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                file.renameTo(new File(parent.getAbsolutePath(), this.name));
            }
            listener.onPostExecute(null, null);
        }
    }

    public void renameLocalByPath(String path) {
        File tmp = new File(path);
        file.renameTo(tmp);
    }

    public void copyFile(String outputPath) {
        copyFile(outputPath, null);
    }

    public void copyFile(String outputPath, IPostExecuteListener listener) {
        if (this.isOnline()) {
            //TODO copy online
            Toast.makeText(mActivity, mActivity.getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
        } else {
            InputStream in;
            OutputStream out;
            try {
                File dir = new File(outputPath);
                if (!dir.exists())
                    dir.mkdirs();

                String outputUrl = outputPath + this.getNameExt();
                while ((new File(outputUrl)).exists()) {
                    outputUrl = outputPath + this.getNameExtCopy();
                }

                if (directory) {
                    File copy = new File(outputUrl);
                    copy.mkdirs();
                    File[] children = file.listFiles();
                    for (int i = 0; i < children.length; i++) {
                        (new ModelFile(mActivity, app, children[i])).copyFile(copy.getAbsolutePath() + File.separator);
                    }
                } else {
                    in = new FileInputStream(this.file.getAbsoluteFile());
                    out = new FileOutputStream(outputUrl);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
        }
        if (listener != null)
            listener.onPostExecute(null, null);
    }

    public boolean isMine() {
        return this.id_user == this.app.getConfig().getUser().id;
    }

    public boolean isAudio() {
        return !(this.directory || this.type == null) && this.type.equals(ModelFileTypeENUM.AUDIO.type);
    }

    public static final Parcelable.Creator<ModelFile> CREATOR = new Parcelable.Creator<ModelFile>() {
        @Override
        public ModelFile createFromParcel(Parcel source) {
            return new ModelFile(source);
        }

        @Override
        public ModelFile[] newArray(int size) {
            return new ModelFile[size];
        }
    };

    public ModelFile(Parcel in) {
        this.id = in.readInt();
        this.url = in.readString();
        this.onlineUrl = in.readString();
        this.name = in.readString();
        this.size = in.readLong();
        boolean[] b = new boolean[1];
        in.readBooleanArray(b);
        this.directory = b[0];
        this.type = new ModelFileType(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.url);
        dest.writeString(this.onlineUrl);
        dest.writeString(this.name);
        dest.writeLong(this.size);
        dest.writeBooleanArray(new boolean[]{this.directory});
        dest.writeString(this.type.getFirstExtension());
    }

    @Override
    public JSONObject toJSONObject() {
        if (this.content != null)
            return this.content.toJSONObject();
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof ModelFile))
            return false;
        ModelFile obj = (ModelFile) o;
        return obj.id == this.id;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        if (file != null) {
            if (file.exists()) {
                this.file = file;
                this.id = file.hashCode();
                this.directory = file.isDirectory();
                this.size = file.length();
                this.url = file.getAbsolutePath();
                int id = file.getName().lastIndexOf(".");
                this.name = (id == -1) ? file.getName() : file.getName().substring(0, id);
                this.type = new ModelFileType(FileUtils.getExtensionFromPath(this.url));
                this.date_creation = new Date(file.lastModified());
                this.mLastModified = file.lastModified();

                if (this.type.equals(ModelFileTypeENUM.PICTURE.type) && this.size >= 0) {
                    (new ImageUtils.LocalBitmapTask(file, new IBitmapListener() {
                        @Override
                        public void execute(Bitmap bitmap) {
                            ModelFile.this.bitmap = bitmap;
                            // TODO refresh list
                        }
                    }, 128, 128)).execute();
                }

                if (this.directory && file.listFiles() != null) {
                    this.count = file.listFiles().length;
                }
            }
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public Spanned toSpanned() {
        List<StringPair> spl = new ArrayList<>();
        spl.add(new StringPair("Name", this.name));
        if (!this.directory) {
            spl.add(new StringPair("Extension", this.type.toString()));
        }
        spl.add(new StringPair("Type", this.type.getTitle()));
        if (!this.directory || this.size != 0) {
            spl.add(new StringPair("Size", FileUtils.humanReadableByteCount(this.size)));
        }
        if (this.date_creation != null) {
            if (this.isOnline()) {
                spl.add(new StringPair("Upload date", TimeUtils.getDate(this.date_creation)));
            } else {
                spl.add(new StringPair("Last modification date", TimeUtils.getDate(this.date_creation)));
            }
        }
        if (this.isOnline())
            spl.add(new StringPair("Visibility", _public ? "Public" : "Private"));
        return HtmlUtils.createListItem(spl);
    }

    public String getName() {
        return name;
    }

    public long length() {
        return size;
    }

    public long lastModified() {
        return mLastModified;
    }
}
