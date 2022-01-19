package parallelmc.parallelutils.modules.charms.data;

import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NonNullListRemember<E> extends NonNullList<E> {

	private List<E> old;

	private final List<E> list;

	@Nullable
	private final E defaultValue;

	public static <E> NonNullListRemember<E> create() {
		return new NonNullListRemember<>(Lists.newArrayList(), (E)null);
	}

	public static <E> NonNullListRemember<E> createWithCapacity(int size) {
		return new NonNullListRemember<>(Lists.newArrayListWithCapacity(size), (E)null);
	}

	public static <E> NonNullListRemember<E> withSize(int size, E defaultValue) {
		Validate.notNull(defaultValue);
		Object[] objects = new Object[size];
		Arrays.fill(objects, defaultValue);
		return new NonNullListRemember<>(Arrays.asList((E[])objects), defaultValue);
	}

	@SafeVarargs
	public static <E> NonNullListRemember<E> of(@NotNull E defaultValue, E... values) {
		return new NonNullListRemember<>(Arrays.asList(values), defaultValue);
	}

	public static <E> NonNullListRemember<E> of(@NotNull NonNullList<E> list) {
		NonNullListRemember<E> nnList = createWithCapacity(list.size());

		for (int i = 0; i < list.size(); i++) {
			nnList.add(i, list.get(i));
		}

		return nnList;
	}

	protected NonNullListRemember(List<E> delegate, @Nullable E initialElement) {
		super(delegate, initialElement);
		this.old = new ArrayList<>();
		this.list = delegate;
		this.defaultValue = initialElement;
	}

	@NotNull
	@Override
	public E get(int i) {
		return this.list.get(i);
	}

	@Nullable
	public E getOld(int i) {
		return this.old.get(i);
	}

	@NotNull
	@Override
	public E set(int i, @NotNull E object) {
		Validate.notNull(object);
		this.old = new ArrayList<>(this.list); // Deep copy
		return this.list.set(i, object);
	}

	@Override
	public void add(int i, @NotNull E object) {
		Validate.notNull(object);
		this.old = new ArrayList<>(this.list); // Deep copy
		this.list.add(i, object);
	}

	@Override
	public E remove(int i) {
		this.old = new ArrayList<>(this.list); // Deep copy
		return this.list.remove(i);
	}

	@Override
	public int size() {
		return this.list.size();
	}

	@Override
	public void clear() {
		this.old = new ArrayList<>(this.list); // Deep copy
		if (this.defaultValue == null) {
			super.clear();
		} else {
			for(int i = 0; i < this.size(); ++i) {
				this.set(i, this.defaultValue);
			}
		}

	}

}
