//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.zju.icsoft.taskcontext.util.jstereocode;

import org.eclipse.jdt.core.dom.ITypeBinding;

import java.util.Comparator;

public class TypeInfo {
    private final ITypeBinding typeBinding;
    private int frequency;

    public TypeInfo(ITypeBinding type) {
        this.typeBinding = type;
        this.frequency = 1;
    }

    public ITypeBinding getTypeBinding() {
        return this.typeBinding;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void incrementFrequency() {
        ++this.frequency;
    }

    public void incrementFrequencyBy(int x) {
        this.frequency += x;
    }

    public int hashCode() {
        int result = 1;
        result = (31 * result) + ((this.typeBinding == null) ? 0 : this.typeBinding.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            TypeInfo other = (TypeInfo) obj;
            if (this.typeBinding == null) {
                return other.typeBinding == null;
            } else return this.typeBinding.equals(other.typeBinding);
        }
    }

    public static class TypeInformationComparator implements Comparator<TypeInfo> {
        public TypeInformationComparator() {
        }

        public int compare(TypeInfo o1, TypeInfo o2) {
            Integer freq1 = o1.getFrequency();
            Integer freq2 = o2.getFrequency();
            return freq2.compareTo(freq1);
        }
    }
}
