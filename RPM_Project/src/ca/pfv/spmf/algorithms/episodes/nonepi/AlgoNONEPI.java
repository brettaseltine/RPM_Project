/*
 * This file is part of the SPMF DATA MINING SOFTWARE *
 * (http://www.philippe-fournier-viger.com/spmf).
 *
 * SPMF is free software: you can redistribute it and/or modify it under the *
 * terms of the GNU General Public License as published by the Free Software *
 * Foundation, either version 3 of the License, or (at your option) any later *
 * version. SPMF is distributed in the hope that it will be useful, but WITHOUT
 * ANY * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * SPMF. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright Oualid Ouarem et al.
 */
package ca.pfv.spmf.algorithms.episodes.nonepi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.pfv.spmf.tools.MemoryLogger;

/**
 * The NONEPI algorithm, which was presented in this paper:
 * 
 * Ouarem, O., Nouioua, F., Fournier-Viger, P. (2021). Mining Episode Rules From
 * Event Sequences Under Non-Overlapping Frequency. Proc. 34th Intern. Conf. on
 * Industrial, Engineering and Other Applications of Applied Intelligent Systems
 * (IEA AIE 2021), Springer LNAI, pp. 73-85.
 * 
 * @author Oualid Ouarem et al.
 */
public class AlgoNONEPI {

	/** Start time of the algorithm */
	private long startExecutionTime;

	/** End time of the algorithm */
	private long endExecutionTime;
//
//	/** List of frequent episodes */
//	private List<Episode> FrequentEpisodes;

	/** List of episode rules */
	private List<String> allRules = new ArrayList<>();

	/** Candidate episode count */
	private int CandidatEpisodesCount;

	/** Number of frequent episodes */
	private int episodeCount;

	/** Maximum size */
	private int maxsize;

	/** Episode rule count */
	private int ruleCount;

	/** Constructor */
	public AlgoNONEPI() {

	}

	/**
	 * Scans the input sequence and return the list of single events episodes
	 *
	 * @param path the path to the file that holds the sequencexs
	 * @throws java.io.IOException if the path is incorrect
	 */
	private Map<String, List<Occurrence>> scanSequence(String path) throws IOException {
		BufferedReader reader;
		reader = new BufferedReader(new FileReader(path));
		String line;
		Map<String, List<Occurrence>> SingleEventEpisode = new HashMap<>();
		while ((line = reader.readLine()) != null) {
			String[] lineSplited = line.split("\\|");
			String event = lineSplited[0];
			long timeStamp = Integer.parseInt(lineSplited[1]);
			List<String> events;
			events = new ArrayList<>();
			events.add(event);
			Episode epi = new Episode(events);
			Occurrence occ = new Occurrence(timeStamp, timeStamp);
			if (SingleEventEpisode.containsKey(epi.toString())) {
				SingleEventEpisode.get(epi.toString()).add(occ);
			} else {
				SingleEventEpisode.put(epi.toString(), new ArrayList<>());
				SingleEventEpisode.get(epi.toString()).add(occ);
			}

		}
		reader.close();
		return SingleEventEpisode;
	}

	/**
	 * Recognize the new episode's occurrences starting from two episodes
	 *
	 * @param alpha       a N-node episode to grow
	 * @param singleEvent a single event to grow alpha by.
	 * @return the list of new episode's occurrences
	 */

	private List<Occurrence> OccurrenceRecognition(Episode alpha, Episode singleEvent) {
		// List<Occurrence> oc_1 = this.FrequentEpisodes.get(alpha);
		// List<Occurrence> oc_2 = this.FrequentEpisodes.get(singleEvent);
		List<Occurrence> oc_1 = alpha.getOccurrences();
		List<Occurrence> oc_2 = singleEvent.getOccurrences();
		List<Occurrence> new_occurrences;
		new_occurrences = new ArrayList<>();
		int i = 0, j = 0, k;
		boolean trouve;
		int taille_1 = oc_1.size(), taille_2;
		while (i < taille_1) {
			// j = 0;
			Occurrence I1 = oc_1.get(i);
			trouve = false;
			k = i + 1;
			taille_2 = oc_2.size();
			while (j < taille_2) {
				Occurrence I2 = oc_2.get(j);
				if (I2.getStart() > I1.getEnd()) {
					Occurrence occ = new Occurrence(I1.getStart(), I2.getEnd());
					new_occurrences.add(occ);
					trouve = true;
					while (k < taille_1) {
						if (oc_1.get(k).getStart() > occ.getEnd()) {
							break;
						}
						k = k + 1;
					}
				}
				if (trouve) {
					break;
				} else {
					j++;
				}

			}
			i = k;
		}
		return new_occurrences;
	}

//    /**
//     * Generate the set of episode rules  from the frequent episodes set
//     *
//     * @param FrequentEpisodes set of frequent episodes already recognized
//     * @param minconf confidence threshold
//     * @return set of all episodes rules
//     */
//    public List<String> NONEpiValidRules(List<Episode> FrequentEpisodes, float minconf) {
//        this.startExecutionTime = System.currentTimeMillis();
//        List<Episode> sizeN;
//        sizeN = new ArrayList<>();
//        for (Episode episode : FrequentEpisodes) {
//            if (episode.getEvents().size() == 1) {
//                sizeN.add(episode);
//            }
//        }
//        List<Episode> rest = FrequentEpisodes.subList(sizeN.size(), FrequentEpisodes.size());
//        int i = 0, length = 2;
////        int size = sizeN.size();
//        boolean stop = false;
//        List<String> allRules = new ArrayList<>();
//        while (!stop) {
//            i = 0;
//            while (i < sizeN.size()) {
//                Episode antecedent;
//                antecedent = sizeN.get(i);
//                System.out.println(antecedent);
//                for (Episode consequent : rest) {
//                    if (!antecedent.toString().equals(consequent.toString())) {
//                        if (antecedent.isPrefix(consequent)) {
//                            if (((float) consequent.getSupport() / (float) antecedent.getSupport()) >= minconf) {
//                                allRules.add(antecedent.toString() + " ==> " + consequent.toString());
//                            }
//                        }
//                    }
//                }
//                i++;
//            }
//            sizeN = new ArrayList<>();
//            for (Episode episode : rest) {
//                if (episode.getEvents().size() == length) {
//                    sizeN.add(episode);
//                }
//            }
////            System.out.println("antecedent  of size = " + length);
////            for (Episode epi : sizeN) {
////                //System.out.println(epi.toString());
////            }
//            if (sizeN.isEmpty()) {
//                stop = true;
//            }
//            length = length + 1;
//        }
//        this.endExecutionTime = System.currentTimeMillis();
//        ruleCount = allRules.size();
//        //  System.out.println("rule generation time "+System.currentTimeMillis()+this.startExecutionTime);
//        return allRules;
//    }

	/**
	 * Generate the set of episode rules from the frequent episodes set (with
	 * pruning)
	 *
	 * @param FrequentEpisodes set of frequent episodes already recognized
	 * @param minconf          confidence threshold
	 * @return set of all episodes rules
	 */
	public List<String> findNONEpiRulesWithPruning(List<Episode> FrequentEpisodes, float minconf) {
		this.startExecutionTime = System.currentTimeMillis();
		allRules = new ArrayList<>();
		for (int i = 0; i < FrequentEpisodes.size(); i++) {
			Episode alpha = FrequentEpisodes.get(i);
			Episode beta = Predecessor(alpha.toString());
			boolean stop = false;
			while (!stop && beta != null) {
				int beta_support = 0;
				for (Episode t_beta : FrequentEpisodes) {
					if (beta.toString().equals(t_beta.toString())) {
						beta_support = t_beta.getSupport();
						break;
					}
				}
				int alpha_support = alpha.getSupport();
				if (((float) alpha_support / (float) beta_support) >= minconf) {
					allRules.add(beta.toSPMFString() + " ==> " + alpha.toSPMFString() 
					+ " #SUP: " + beta_support 
					+ " #CONF: "
							+ (float) alpha_support / (float) beta_support);
					beta = Predecessor(beta.toString());
				} else {
					stop = true;
				}
			}
		}
		this.endExecutionTime = System.currentTimeMillis();
		this.ruleCount = allRules.size();
		return allRules;
	}

	/**
	 * Generate new Episodes and filter only the frequent ones
	 *
	 * @param input      A sequence of events
	 * @param minsupport Support threshold
	 * @return f_episode list of all frequent episodes
	 * @throws IOException If the path is incorrect or the file doesn't exists
	 */
	public List<Episode> findFrequentEpisodes(String input, int minsupport) throws IOException {
		MemoryLogger.getInstance().reset();
		List<Episode> f_episode = new ArrayList<>();
		this.startExecutionTime = System.currentTimeMillis();
		Map<String, List<Occurrence>> singleEpisodeEvent;
		singleEpisodeEvent = scanSequence(input);
		Object[] episodes = singleEpisodeEvent.keySet().toArray();
		this.CandidatEpisodesCount = episodes.length;
		// this.FrequentEpisodes = new HashMap<>();
		for (Object episode : episodes) {
			int t_sup = singleEpisodeEvent.get(episode.toString()).size();
			if (t_sup >= minsupport) {
				List<String> t_events = new ArrayList<>();
				Collections.addAll(t_events, StrToList(episode.toString()));
				Episode t_epi = new Episode(t_events);
				List<Occurrence> occurrences = singleEpisodeEvent.get(episode.toString());
				t_epi.setOccurrences(occurrences);
				t_epi.setSupport(t_sup);
				f_episode.add(t_epi);
				// this.FrequentEpisodes.put(episode.toString(),
				// singleEpisodeEvent.get(episode.toString()));
			}
		}
		// Map<String, List<Occurrence>> t_freq = this.FrequentEpisodes;
		List<Episode> t_freq = f_episode;
		// episodes = t_freq.keySet().toArray();
		int i = 0, j;
		int thesize = t_freq.size();
		while (i < thesize) {
			j = 0;
			// String alpha = episodes[i].toString();
			Episode alpha = t_freq.get(i);
			while (j < thesize) {
				List<String> newEvents = new ArrayList<>();
				Collections.addAll(newEvents, StrToList(alpha.toString()));
				newEvents.add(newEvents.size(), StrToList(t_freq.get(j).toString())[0]);
				Episode new_epi = new Episode(newEvents);
				CandidatEpisodesCount++;
				if (isInjective(newEvents)) {
					// List<Occurrence> newOccurrences = OccurrenceRecognition(alpha,
					// episodes[j].toString());

					List<Occurrence> newOccurrences = OccurrenceRecognition(alpha, t_freq.get(j));

					int t_support = newOccurrences.size();
					if (t_support >= minsupport) {

						new_epi.setOccurrences(newOccurrences);
						new_epi.setSupport(t_support);
						// this.FrequentEpisodes.put(new_epi.toString(), newOccurrences);
						f_episode.add(new_epi);
						alpha = new_epi;
						if (new_epi.getEvents().size() >= maxsize) {
							maxsize = new_epi.getEvents().size();
						}
					}
				}
				j++;
			}
			i++;
		}
		this.endExecutionTime = System.currentTimeMillis();
		MemoryLogger.getInstance().checkMemory();
		episodeCount = f_episode.size();
		return f_episode;
	}

	/**
	 * Check if it is injective
	 * 
	 * @param events a list of events
	 * @return true if injective, otherwise false
	 */
	private boolean isInjective(List<String> events) {
		if (events.isEmpty()) {
			return true;
		}
		String event = events.get(events.size() - 1);
		events = events.subList(0, events.size() - 1);
		if (events.contains(event)) {
			return false;
		}
		return isInjective(events);
	}

	/**
	 * Get the predecessor
	 * 
	 * @param alpha
	 * @return the predecessor episode or null
	 */
	private Episode Predecessor(String alpha) {
		String[] temp_alpha = StrToList(alpha);
		if (temp_alpha.length != 1) {
			String[] t_predecessor = new String[temp_alpha.length - 1];
			for (int i = 0; i < t_predecessor.length; i++) {
				t_predecessor[i] = temp_alpha[i];
			}
			List<String> events = new ArrayList<>();
			Collections.addAll(events, t_predecessor);
			return new Episode(events);
		}
		return null;
	}

	/**
	 * Convert a String to an array of String
	 * 
	 * @param string the string
	 * @return an array of String
	 */
	private static String[] StrToList(String string) {
		int index_1 = string.indexOf("<");
		String tempString = string.substring(index_1 + 1, string.length() - 1);
		if (tempString.contains("->")) {
			return tempString.split("->");
		}
		return new String[] { tempString };
	}

//	/**
//	 * Get the frequent episodes
//	 * 
//	 * @return the list of frequent episodes
//	 */
//	public List<Episode> getFrequentEpisodes() {
//		return this.FrequentEpisodes;
//	}

	/**
	 * Print statistics about the last execution of the algorithm.
	 */
	public void printStats() {
		System.out.println("=============  NONEPI - STATS =============");
		System.out.println(" Candidates count : " + this.CandidatEpisodesCount);
		System.out.println(" The algorithm stopped at size : " + maxsize);
		System.out.println(" Frequent episodes count : " + episodeCount);
		System.out.println(" Maximum memory usage : " + MemoryLogger.getInstance().getMaxMemory() + " mb");
		System.out.println(" Total time ~ : " + (this.endExecutionTime - this.startExecutionTime) + " ms");
		System.out.println(" Episode rule count: " + this.ruleCount);
		System.out.println("===================================================");
	}

	/**
	 * Save the rules to a file
	 * 
	 * @param outputPath the output file path
	 */
	public void saveRulesToFile(String outputPath) {
		try {
			PrintWriter writer = new PrintWriter(outputPath, "UTF-8");
			writer.write(rulesAsString());
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a string representation of this set of rules (for printing or writing to
	 * file)
	 * 
	 * @return a string
	 */
	private String rulesAsString() {
		StringBuilder buffer = new StringBuilder();

		// For each rule
		for (int z = 0; z < allRules.size(); z++) {
			String rule = allRules.get(z);

			buffer.append(rule);
			buffer.append(System.lineSeparator());
		}
		return buffer.toString();
	}
}
