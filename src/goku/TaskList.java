package goku;

import java.util.ArrayList;
import java.util.Iterator;

public class TaskList implements Iterable<Task> {
  private static Integer count = 0;

  private ArrayList<Task> _list;

  public TaskList() {
    _list = new ArrayList<Task>();
  }

  public boolean addTask(Task task) {
    task.setId(++count);
    return _list.add(task);
  }

  public boolean addTaskWithoutSettingId(Task task) {
    if (getTaskById(task.getId()) == null) {
      return _list.add(task);
    }
    return false;
  }

  public boolean appendTask(Task task) {
    return _list.add(task);
  }

  public void addUndoTask(Task task) {
    _list.add(task);
  }

  public void clear() {
    _list.clear();
  }

  private TaskList deleteTask(TaskList matches) {
    if (matches.size() == 1) {
      deleteTaskById(matches.getTaskByIndex(0).getId());
      return new TaskList();
    } else {
      return matches;
    }
  }

  public Task deleteTaskById(int id) {
    int index = getIndexOfTaskById(id);
    if (index < 0) {
      return null;
    } else {
      return deleteTaskByIndex(index);
    }
  }

  public Task deleteTaskByIndex(int index) {
    return _list.remove(index);
  }

  public TaskList deleteTaskByTitle(Task toDelete) {
    TaskList matches = findTaskByTitle(toDelete);
    return deleteTask(matches);
  }

  public TaskList findTaskByDeadline(Task toFind) {
    TaskList matches = new TaskList();
    for (Task task : _list) {
      if (task.getDeadline() == null) {
        continue;
      }
      if (task.isDueBefore(toFind)) {
        matches.appendTask(task);
      }
    }
    return matches;

  }

  public TaskList findTaskByTags(Task toFind) {
    TaskList matches = new TaskList();
    for (Task task : _list) {
      if (task.tagsMatch(toFind)) {
        matches.appendTask(task);
      }
    }
    return matches;
  }

  public TaskList findTaskByTitle(Task toFind) {
    TaskList matches = new TaskList();
    for (Task task : _list) {
      if (task.titleMatches(toFind)) {
        matches.appendTask(task);
      }
    }
    return matches;
  }

  public TaskList getAll() {
    return this;
  }

  public TaskList getAllCompleted() {
    TaskList result = new TaskList();
    for (Task task : _list) {
      if (task.getStatus()) {
        result.addTask(task);
      }
    }
    return result;
  }

  public TaskList getAllIncomplete() {
    TaskList result = new TaskList();
    for (Task task : _list) {
      if (!task.getStatus()) {
        result.addTask(task);
      }
    }
    return result;
  }

  public ArrayList<Task> getArrayList() {
    return _list;
  }

  private int getIndexOfTaskById(int id) {
    return _list.indexOf(getTaskById(id));
  }

  /*
   * @param id
   * 
   * @returns the task with specified id
   */
  public Task getTaskById(int id) {
    for (Task task : _list) {
      if (task.getId() == id) {
        return task;
      }
    }
    return null;
  }

  protected Task getTaskByIndex(int index) {
    return _list.get(index);
  }

  @Override
  public Iterator<Task> iterator() {
    return _list.listIterator();
  }

  public int size() {
    return _list.size();
  }

}
