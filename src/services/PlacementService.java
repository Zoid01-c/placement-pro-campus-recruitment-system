package services;

import models.PlacementDrive;
import models.Student;
import utils.FileManager;

import java.util.*;

/**
 * Service managing placement drives, search queries, and eligibility calculations.
 * Demonstrates Collections (List, ArrayList, HashMap, HashSet, TreeSet, Comparator).
 */
public class PlacementService {

    private List<PlacementDrive> drives;
    private Map<String, PlacementDrive> driveMap; // Fast lookup by driveId

    public PlacementService() {
        this.drives = new ArrayList<>();
        this.driveMap = new HashMap<>();
        reload();
    }

    public void reload() {
        this.drives = FileManager.loadDrives();
        this.driveMap.clear();
        for (PlacementDrive d : this.drives) {
            this.driveMap.put(d.getDriveId(), d);
        }
    }

    public void saveAll() {
        FileManager.saveDrives(this.drives);
    }

    public List<PlacementDrive> getAllDrives() {
        return Collections.unmodifiableList(this.drives);
    }

    public List<PlacementDrive> getActiveDrives() {
        List<PlacementDrive> activeList = new ArrayList<>();
        for (PlacementDrive d : this.drives) {
            if (d.isActive()) {
                activeList.add(d);
            }
        }
        return activeList;
    }

    public PlacementDrive getDriveById(String driveId) {
        if (driveId == null) return null;
        return driveMap.get(driveId.trim().toUpperCase());
    }

    public void addDrive(PlacementDrive drive) {
        if (drive != null) {
            this.drives.add(drive);
            this.driveMap.put(drive.getDriveId(), drive);
            saveAll();
        }
    }

    public boolean updateDrive(PlacementDrive updated) {
        if (updated == null) return false;
        for (int i = 0; i < drives.size(); i++) {
            if (drives.get(i).getDriveId().equalsIgnoreCase(updated.getDriveId())) {
                drives.set(i, updated);
                driveMap.put(updated.getDriveId(), updated);
                saveAll();
                return true;
            }
        }
        return false;
    }

    public boolean toggleDriveStatus(String driveId) {
        PlacementDrive d = getDriveById(driveId);
        if (d != null) {
            d.setActive(!d.isActive());
            saveAll();
            return true;
        }
        return false;
    }

    public List<PlacementDrive> getDrivesByCompany(String companyId) {
        List<PlacementDrive> list = new ArrayList<>();
        for (PlacementDrive d : this.drives) {
            if (d.getCompanyId().equalsIgnoreCase(companyId)) {
                list.add(d);
            }
        }
        return list;
    }

    /**
     * Determines if a student satisfies all academic eligibility criteria for a drive.
     */
    public boolean isStudentEligible(Student student, PlacementDrive drive) {
        if (student == null || drive == null || !drive.isActive()) {
            return false;
        }

        // 1. CGPA Criterion
        if (student.getCgpa() < drive.getMinCGPA()) {
            return false;
        }

        // 2. Backlogs Criterion
        if (student.getBacklogs() > drive.getMaxBacklogs()) {
            return false;
        }

        // 3. Branch Eligibility (case-insensitive membership)
        boolean branchMatch = false;
        if (drive.getEligibleBranches().isEmpty()) {
            branchMatch = true; // open to all
        } else {
            for (String b : drive.getEligibleBranches()) {
                if (b.equalsIgnoreCase(student.getBranch())) {
                    branchMatch = true;
                    break;
                }
            }
        }

        return branchMatch;
    }

    /**
     * Filters active drives for only those the student is eligible for.
     */
    public List<PlacementDrive> getEligibleDrives(Student student) {
        List<PlacementDrive> eligible = new ArrayList<>();
        for (PlacementDrive d : getActiveDrives()) {
            if (isStudentEligible(student, d)) {
                eligible.add(d);
            }
        }
        return eligible;
    }

    // ==========================================
    // SEARCH CAPABILITIES
    // ==========================================
    public List<PlacementDrive> searchByCompany(String keyword) {
        String kw = keyword.toLowerCase();
        List<PlacementDrive> result = new ArrayList<>();
        for (PlacementDrive d : this.drives) {
            if (d.getCompanyName().toLowerCase().contains(kw)) {
                result.add(d);
            }
        }
        return result;
    }

    public List<PlacementDrive> searchByRole(String keyword) {
        String kw = keyword.toLowerCase();
        List<PlacementDrive> result = new ArrayList<>();
        for (PlacementDrive d : this.drives) {
            if (d.getJobRole().toLowerCase().contains(kw)) {
                result.add(d);
            }
        }
        return result;
    }

    public List<PlacementDrive> searchByLocation(String keyword) {
        String kw = keyword.toLowerCase();
        List<PlacementDrive> result = new ArrayList<>();
        for (PlacementDrive d : this.drives) {
            if (d.getLocation().toLowerCase().contains(kw)) {
                result.add(d);
            }
        }
        return result;
    }

    // ==========================================
    // COMPARATORS & SORTING
    // ==========================================
    public List<PlacementDrive> getDrivesSortedByPackage(boolean descending) {
        List<PlacementDrive> sorted = new ArrayList<>(this.drives);
        sorted.sort(new Comparator<PlacementDrive>() {
            @Override
            public int compare(PlacementDrive o1, PlacementDrive o2) {
                int cmp = Double.compare(o1.getPackageLPA(), o2.getPackageLPA());
                return descending ? -cmp : cmp;
            }
        });
        return sorted;
    }

    public int getDriveCount() {
        return this.drives.size();
    }
}
