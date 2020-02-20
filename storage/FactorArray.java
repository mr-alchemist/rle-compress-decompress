package storage;

public class FactorArray<T> implements IDynamicArray<T> {

	Object[] array;
	int size;
	int factor;
	Stack<Integer> lengthHist;
	
	public FactorArray() {
		factor = 100;
		array = new Object[10];
		size = 0;
		lengthHist = new Stack<Integer>();
	}
	
	@Override
	public int size() {
		return size;
	}
	
	
	@Override
	public void add(T item) {
		if(size() == array.length )
			resize_plus();
		array[size] = item;
		size++;
	}
	
	private void resize_plus() {
		Object[] newArray = new Object[array.length + (int)(array.length* factor/100.0) ];
		System.arraycopy(array, 0, newArray, 0, size());
		lengthHist.push(array.length);
		array = newArray;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T get(int index) {
		return (T)array[index];
	}	
	
	@Override
	public void add(T item, int index) {
		if(index > size() || index < 0)throw new IndexOutOfBoundsException();
		if(size() == array.length ) 
			resize_plus(index);
		else {
			/*for(int i = size() - 1; i >= index   ; i--) 
				array[i+1] = array[i];*/
			System.arraycopy(array, index, array, index + 1, size() - index);
		}
		array[index] = item;
		size++;
	}
	
	private void resize_plus(int index) {
		Object[] newArray = new Object[array.length + (int)(array.length* factor/100.0)];
		System.arraycopy(array, 0, newArray, 0, index);
		System.arraycopy(array, index, newArray, index + 1, size() - index);
		lengthHist.push(array.length);
		array = newArray;
	}
	
	@Override
	public T remove(int index) {
		if(index >= size())throw new IndexOutOfBoundsException();
		T item = get(index);
		if( array.length > 10  //т.е. динамич. массив увеличивался как минимум 1 раз
				&& lengthHist.peek() == (size() - 1))//в массив предыдущего размера новое количество элементов "влезет"
			resize_minus(index);
		
		else {
			System.arraycopy(array, index + 1, array, index, size() - index - 1);
			array[size() - 1] = null;
		}
		size--;
		return item;
	}
	
	private void resize_minus(int index) {
		Object[] newArray = new Object[lengthHist.pop()];
		System.arraycopy(array, 0, newArray, 0, index);
		System.arraycopy(array, index+1, newArray, index, size() - index - 1);
		array = newArray;
	}
	
	public void clear() {
		array = new Object[10];
		size = 0;
		lengthHist = new Stack<Integer>();
	}
	
}
