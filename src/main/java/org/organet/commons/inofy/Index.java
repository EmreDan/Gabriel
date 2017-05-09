package org.organet.commons.inofy;

import org.organet.commons.inofy.Model.SharedFileHeader;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// TODO `implements Serializable` or `.serialize()`
public class Index implements Serializable {
  private ArrayList<SharedFileHeader> sharedFileHeaders;
  private boolean isLocal;

  public Index(boolean isLocal) {
    sharedFileHeaders = new ArrayList<>();
    this.isLocal = isLocal;
  }

  public ArrayList<SharedFileHeader> getSharedFileHeaders() {
    return sharedFileHeaders;
  }

  public int size() {
    return sharedFileHeaders.size();
  }

  // Performance O(N)
  public boolean contains(String ndnid) {
    for (SharedFileHeader sharedFileHeader : sharedFileHeaders) {
      if (sharedFileHeader.getNDNID().equals(ndnid)) {
        return true;
      }
    }

    return false;
  }

  // TODO Other 'contains' methods may be implemented \
  // (e.g. `.contains(SharedFileHeader ...)` or `.contains(File ...`)

  public boolean add(SharedFileHeader sharedFileHeader) {
    return sharedFileHeaders.add(sharedFileHeader);
    // TODO Invoke the necessary method to propagate the updated index (if the index is local)
    // TODO TR Future work'e yaz: Sadece değişiklikler propagate edilebilir
  }

  public boolean remove(SharedFileHeader sharedFileHeader) {
    return sharedFileHeaders.remove(sharedFileHeader);
    // TODO Invoke the necessary method to propagate the updated index (if the index is local)
    // TODO TR Future work'e yaz: Sadece değişiklikler propagate edilebilir
  }

  public void clear() {
    sharedFileHeaders.clear();
    // TODO Invoke the necessary method to propagate the updated index (if the index is local)
    // TODO TR Future work'e yaz: Sadece değişiklikler propagate edilebilir
  }

  public SharedFileHeader get(int index) {
    return sharedFileHeaders.get(index);
  }

  public SharedFileHeader get(String ndnid) {
    for (SharedFileHeader sharedFileHeader : sharedFileHeaders) {
      if (sharedFileHeader.getNDNID().equals(ndnid)) {
        return sharedFileHeader;
      }
    }

    return null;
  }

  public SharedFileHeader remove(String ndnid) {
    SharedFileHeader itemToBeRemoved = null;

    for (int i = 0, len = sharedFileHeaders.size(); i < len; i++) {
      if (sharedFileHeaders.get(i).getNDNID().equals(ndnid)) {
        itemToBeRemoved = sharedFileHeaders.get(i);
        sharedFileHeaders.remove(i);

        break;
      }
    }

    return itemToBeRemoved;
    // TODO Invoke the necessary method to propagate the updated index (if the index is local)
    // TODO TR Future work'e yaz: Sadece değişiklikler propagate edilebilir
  }

  public SharedFileHeader remove(Path filename) {
    SharedFileHeader itemToBeRemoved = null;

    for (int i = 0, len = sharedFileHeaders.size(); i < len; i++) {
      if (sharedFileHeaders.get(i).getPath().equals(filename)) {
        itemToBeRemoved = sharedFileHeaders.get(i);
        sharedFileHeaders.remove(i);

        break;
      }
    }

    return itemToBeRemoved;
    // TODO Invoke the necessary method to propagate the updated index (if the index is local)
    // TODO TR Future work'e yaz: Sadece değişiklikler propagate edilebilir
  }

  public int indexOf(String ndnid) {
    for (int i = 0, len = sharedFileHeaders.size(); i < len; i++) {
      if (sharedFileHeaders.get(i).getNDNID().equals(ndnid)) {
        return i;
      }
    }

    return -1;
  }

  // NOTE This method can be considered `.set(int, element)` of List interface
  public boolean update(String ndnid, SharedFileHeader sharedFileHeader) {
    int index = indexOf(ndnid);

    if (index > -1) {
      // exists - replace with the given one
      return (sharedFileHeaders.set(index, sharedFileHeader).getNDNID().equals(ndnid));
    } else {
      // doesn't exist - add the given shared file
      return add(sharedFileHeader);
    }
    // TODO Invoke the necessary method to propagate the updated index (if the index is local)
    // TODO TR Future work'e yaz: Sadece değişiklikler propagate edilebilir
  }

  public List<SharedFileHeader> search(String keyword) {
    List<SharedFileHeader> foundSharedFileHeaders = new ArrayList<>();

    for (SharedFileHeader sharedFileHeader : sharedFileHeaders) {
      if (sharedFileHeader.hasKeyword(keyword)) {
        foundSharedFileHeaders.add(sharedFileHeader);
      }
    }

    return foundSharedFileHeaders;
  }

  @Override
  public String toString() {
    return "Index{" +
            "sharedFileHeaders=" + sharedFileHeaders +
            '}';
  }

  public void addAllSharedFiles(Index newIndex){
    for (int i=0;i<newIndex.size();i++){
      this.add(newIndex.get(i));
    }
  }

  public SharedFileHeader findIndex(String selectedFileScreenName) {
    for (SharedFileHeader sh :
            this.getSharedFileHeaders()) {
      if (sh.getScreenName().equals(selectedFileScreenName))
        return sh;
    }

    System.out.println("SharedFileHeader couldn't find on networkIndex");
    return null;
  }

}
