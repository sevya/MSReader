package mass.spec;

public enum Modification {
    OXIDATION (15.99491), DEAMIDATION (0.98402), BIOTINYLATION (226.07760);
    
    private double massShift;
    
    Modification(double massShift) {
        this.massShift = massShift;
    }
}
