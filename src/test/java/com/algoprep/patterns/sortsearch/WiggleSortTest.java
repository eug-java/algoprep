package com.algoprep.patterns.sortsearch;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class WiggleSortTest { @Test void arrangesWiggleOrder() { int[] nums = {3,5,2,1,6,4}; WiggleSort.wiggleSort(nums); for (int i = 1; i < nums.length; i++) assertTrue(i % 2 == 1 ? nums[i] >= nums[i-1] : nums[i] <= nums[i-1]); } @Test void handlesDuplicates() { int[] nums = {1,1,1}; WiggleSort.wiggleSort(nums); assertArrayEquals(new int[] {1,1,1}, nums); } }
