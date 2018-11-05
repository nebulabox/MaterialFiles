/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.materialfilemanager.filelist;

import android.os.Parcelable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import me.zhanghai.android.materialfilemanager.filesystem.File;
import me.zhanghai.android.materialfilemanager.filesystem.Files;

public class FileListViewModel extends ViewModel {

    @NonNull
    private final TrailLiveData mTrailLiveData = new TrailLiveData();
    @NonNull
    private final LiveData<File> mCurrentFileLiveData = Transformations.map(mTrailLiveData,
            TrailData::getCurrentFile);
    @NonNull
    private final LiveData<FileListData> mFileListLiveData = Transformations.switchMap(
            mCurrentFileLiveData, FileListLiveData::new);
    @NonNull
    private final LiveData<BreadcrumbData> mBreadcrumbLiveData = new BreadcrumbLiveData(
            mTrailLiveData);
    @NonNull
    private final MutableLiveData<Set<File>> mSelectedFilesLiveData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<FilePasteMode> mPasteModeLiveData = new MutableLiveData<>();

    public FileListViewModel() {
        mSelectedFilesLiveData.setValue(new HashSet<>());
        mPasteModeLiveData.setValue(FilePasteMode.NONE);
        // FIXME: Handle multi instances.
        mTrailLiveData.observeForever(trailData -> Files.onTrailChanged(trailData.getTrail()));
    }

    public boolean hasTrail() {
        return mTrailLiveData.getValue() != null;
    }

    public void navigateTo(@NonNull Parcelable lastState, @NonNull File file) {
        mTrailLiveData.navigateTo(lastState, file);
    }

    public void resetTo(@NonNull File file) {
        mTrailLiveData.resetTo(file);
    }

    public boolean navigateUp(boolean overrideBreadcrumb) {
        if (!overrideBreadcrumb && mBreadcrumbLiveData.getValue().selectedIndex == 0) {
            return false;
        }
        return mTrailLiveData.navigateUp();
    }

    @Nullable
    public Parcelable getPendingState() {
        return mTrailLiveData.getValue().getPendingState();
    }

    public void reload() {
        Files.invalidateCache(mTrailLiveData.getValue().getCurrentFile());
        mTrailLiveData.reload();
    }

    @NonNull
    public LiveData<File> getCurrentFileLiveData() {
        return mCurrentFileLiveData;
    }

    @NonNull
    public File getCurrentFile() {
        return mCurrentFileLiveData.getValue();
    }

    @NonNull
    public LiveData<FileListData> getFileListLiveData() {
        return mFileListLiveData;
    }

    @NonNull
    public FileListData getFileListData() {
        return mFileListLiveData.getValue();
    }

    @NonNull
    public LiveData<BreadcrumbData> getBreadcrumbLiveData() {
        return mBreadcrumbLiveData;
    }

    @NonNull
    public LiveData<Set<File>> getSelectedFilesLiveData() {
        return mSelectedFilesLiveData;
    }

    @NonNull
    public Set<File> getSelectedFiles() {
        return mSelectedFilesLiveData.getValue();
    }

    public void selectFile(@NonNull File file, boolean selected) {
        selectFiles(Collections.singleton(file), selected);
    }

    public void selectFiles(@NonNull Set<File> files, boolean selected) {
        Set<File> selectedFiles = mSelectedFilesLiveData.getValue();
        if (selectedFiles == files) {
            if (!selected && !selectedFiles.isEmpty()) {
                selectedFiles.clear();
                mSelectedFilesLiveData.setValue(selectedFiles);
            }
            return;
        }
        boolean changed = false;
        for (File file : files) {
            changed |= selected ? selectedFiles.add(file) : selectedFiles.remove(file);
        }
        if (changed) {
            mSelectedFilesLiveData.setValue(selectedFiles);
        }
    }

    public void selectAllFiles() {
        List<File> fileList = mFileListLiveData.getValue().fileList;
        if (fileList == null) {
            return;
        }
        selectFiles(new HashSet<>(fileList), true);
    }

    public void replaceSelectedFiles(@NonNull Set<File> files) {
        Set<File> selectedFiles = mSelectedFilesLiveData.getValue();
        if (selectedFiles.equals(files)) {
            return;
        }
        selectedFiles.clear();
        selectedFiles.addAll(files);
        mSelectedFilesLiveData.setValue(selectedFiles);
    }

    public void clearSelectedFiles() {
        Set<File> selectedFiles = mSelectedFilesLiveData.getValue();
        if (selectedFiles.isEmpty()) {
            return;
        }
        selectedFiles.clear();
        mSelectedFilesLiveData.setValue(selectedFiles);
    }

    @NonNull
    public LiveData<FilePasteMode> getPasteModeLiveData() {
        return mPasteModeLiveData;
    }

    @NonNull
    public FilePasteMode getPasteMode() {
        return mPasteModeLiveData.getValue();
    }

    public void setPasteMode(@NonNull FilePasteMode pasteMode) {
        mPasteModeLiveData.setValue(pasteMode);
    }
}
