package datamodel;

/**
 * Test class
 * 
 * @author Collin Shoop
 */
public class ComplexTypeA {

	private String name;
	private int length;
	private String[] extras;
	private ComplexTypeB complex;
	private Boolean isValid;

	public ComplexTypeA() {

	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @param extras the extras to set
	 */
	public void setExtras(String[] extras) {
		this.extras = extras;
	}

	/**
	 * @param complex the complex to set
	 */
	public void setComplex(ComplexTypeB complex) {
		this.complex = complex;
	}

	/**
	 * @param isValid the isValid to set
	 */
	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return the extras
	 */
	public String[] getExtras() {
		return extras;
	}

	/**
	 * @return the complex
	 */
	public ComplexTypeB getComplex() {
		return complex;
	}

	/**
	 * @return the isValid
	 */
	public Boolean getIsValid() {
		return isValid;
	}

}
