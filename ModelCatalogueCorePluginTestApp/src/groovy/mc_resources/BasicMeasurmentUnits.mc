skip draft


dataModel name: 'SI', {
    // the units are already there but unclassified
    globalSearchFor measurementUnit

    id 'http://www.bipm.org/en/publications/si-brochure/'
    description 'The International System of Units (SI)'


    measurementUnit name: 'meter', symbol: 'm', {
        id 'http://www.bipm.org/en/publications/si-brochure/metre.html'
        description '''
            The 1889 definition of the metre, based on the international prototype of platinum-iridium, was replaced by the 11th CGPM (1960) using a definition based on the wavelength of krypton 86 radiation. This change was adopted in order to improve the accuracy with which the definition of the metre could be realized, the realization being achieved using an interferometer with a travelling microscope to measure the optical path difference as the fringes were counted. In turn, this was replaced in 1983 by the 17th CGPM (1983, Resolution 1) that specified the current definition, as follows:

            The metre is the length of the path travelled by light in vacuum during a time interval of 1/299 792 458 of a second.

            It follows that the speed of light in vacuum is exactly 299 792 458 metres per second, c0 = 299 792 458 m/s.

            The original international prototype of the metre, which was sanctioned by the 1st CGPM in 1889, is still kept at the BIPM under conditions specified in 1889.
        '''
    }

    measurementUnit name: 'kilogram', symbol: 'kg', {
        id 'http://www.bipm.org/en/publications/si-brochure/kilogram.html'
        description '''
            The international prototype of the kilogram, an artefact made of platinum-iridium, is kept at the BIPM under the conditions specified by the 1st CGPM in 1889 when it sanctioned the prototype and declared:

            This prototype shall henceforth be considered to be the unit of mass.
            The 3rd CGPM (1901), in a declaration intended to end the ambiguity in popular usage concerning the use of the word "weight", confirmed that:

            The kilogram is the unit of mass; it is equal to the mass of the international prototype of the kilogram.
            The complete declaration appears here.

            It follows that the mass of the international prototype of the kilogram is always 1 kilogram exactly, m(grand K) = 1 kg. However, due to the inevitable accumulation of contaminants on surfaces, the international prototype is subject to reversible surface contamination that approaches 1 µg per year in mass. For this reason, the CIPM declared that, pending further research, the reference mass of the international prototype is that immediately after cleaning and washing by a specified method (PV, 1989, 57, 104-105 and PV, 1990, 58, 95-97). The reference mass thus defined is used to calibrate national standards of platinum-iridium alloy (Metrologia, 1994, 31, 317-336).
        '''
    }

    measurementUnit name: 'second', symbol: 's', {
        id 'http://www.bipm.org/en/publications/si-brochure/second.html'
        description '''
            The unit of time, the second, was at one time considered to be the fraction 1/86 400 of the mean solar day. The exact definition of "mean solar day" was left to the astronomers. However measurements showed that irregularities in the rotation of the Earth made this an unsatisfactory definition. In order to define the unit of time more precisely, the 11th CGPM (1960, Resolution 9) adopted a definition given by the International Astronomical Union based on the tropical year 1900. Experimental work, however, had already shown that an atomic standard of time, based on a transition between two energy levels of an atom or a molecule, could be realized and reproduced much more accurately. Considering that a very precise definition of the unit of time is indispensable for science and technology, the 13th CGPM (1967/68, Resolution 1) replaced the definition of the second by the following:

            The second is the duration of 9 192 631 770 periods of the radiation corresponding to the transition between the two hyperfine levels of the ground state of the caesium 133 atom.
            It follows that the hyperfine splitting in the ground state of the caesium 133 atom is exactly 9 192 631 770 hertz, nu(hfs Cs) = 9 192 631 770 Hz.

            At its 1997 meeting the CIPM affirmed that:

            This definition refers to a caesium atom at rest at a temperature of 0 K.
            This note was intended to make it clear that the definition of the SI second is based on a caesium atom unperturbed by black body radiation, that is, in an environment whose thermodynamic temperature is 0 K. The frequencies of all primary frequency standards should therefore be corrected for the shift due to ambient radiation, as stated at the meeting of the Consultative Committee for Time and Frequency in 1999.
        '''
    }

    measurementUnit name: 'ampere', symbol: 'A', {
        id 'http://www.bipm.org/en/publications/si-brochure/ampere.html'
        description '''
            Electric units, called "international units", for current and resistance, were introduced by the International Electrical Congress held in Chicago in 1893, and definitions of the "international ampere" and "international ohm" were confirmed by the International Conference in London in 1908.

            Although it was already obvious on the occasion of the 8th CGPM (1933) that there was a unanimous desire to replace those "international units" by so-called "absolute units", the official decision to abolish them was only taken by the 9th CGPM (1948), which adopted the ampere for the unit of electric current, following a definition proposed by the CIPM (1946, Resolution 2):

            The ampere is that constant current which, if maintained in two straight parallel conductors of infinite length, of negligible circular cross-section, and placed 1 metre apart in vacuum, would produce between these conductors a force equal to 2 x 10–7 newton per metre of length.
            It follows that the magnetic constant, mu0, also known as the permeability of free space, is exactly 4 x 10–7 henries per metre, mu0 = 4 x 10–7 H/m.

            The expression "MKS unit of force" which occurs in the original text of 1946 has been replaced here by "newton", a name adopted for this unit by the 9th CGPM (1948, Resolution 7).
        '''
    }

    measurementUnit name: 'kelvin', symbol: 'K', {
        id 'http://www.bipm.org/en/publications/si-brochure/kelvin.html'
        description '''
            The kelvin, unit of thermodynamic temperature, is the fraction 1/273.16 of the thermodynamic temperature of the triple point of water.
            It follows that the thermodynamic temperature of the triple point of water is exactly 273.16 kelvins, Ttpw = 273.16 K.

            The symbol, Ttpw, is used to denote the thermodynamic temperature of the triple point of water.
            At its 2005 meeting the CIPM affirmed that:

            This definition refers to water having the isotopic composition defined exactly by the following amount of substance ratios: 0.000 155 76 mole of 2H per mole of 1H, 0.000 379 9 mole of 17O per mole of 16O, and 0.002 005 2 mole of 18O per mole of 16O.
            Because of the manner in which temperature scales used to be defined, it remains common practice to express a thermodynamic temperature, symbol T, in terms of its difference from the reference temperature T0 = 273.15 K, the ice point. This difference is called the Celsius temperature, symbol t, which is defined by the quantity equation:

            t = T – T0.
            The unit of Celsius temperature is the degree Celsius, symbol °C, which is by definition equal in magnitude to the kelvin. A difference or interval of temperature may be expressed in kelvins or in degrees Celsius (13th CGPM, 1967/68, Resolution 3, mentioned above), the numerical value of the temperature difference being the same. However, the numerical value of a Celsius temperature expressed in degrees Celsius is related to the numerical value of the thermodynamic temperature expressed in kelvins by the relation

            t/°C = T/K – 273.15.
        '''
    }

    measurementUnit name: 'mole', symbol: 'mol', {
        id 'http://www.bipm.org/en/publications/si-brochure/mole.html'
        description '''
            The quantity used by chemists to specify the amount of chemical elements or compounds is now called "amount of substance". Amount of substance is defined to be proportional to the number of specified elementary entities in a sample, the proportionality constant being a universal constant which is the same for all samples. The unit of amount of substance is called the mole, symbol mol, and the mole is defined by specifying the mass of carbon 12 that constitutes one mole of carbon 12 atoms. By international agreement this was fixed at 0.012 kg, i.e. 12 g.

            Following proposals by the IUPAP, the IUPAC, and the ISO, the CIPM gave a definition of the mole in 1967 and confirmed it in 1969. This was adopted by the 14th CGPM (1971, Resolution 3):

            The mole is the amount of substance of a system which contains as many elementary entities as there are atoms in 0.012 kilogram of carbon 12; its symbol is "mol".
            When the mole is used, the elementary entities must be specified and may be atoms, molecules, ions, electrons, other particles, or specified groups of such particles.
            It follows that the molar mass of carbon 12 is exactly 12 grams per mole, M(12C) = 12 g/mol.

            In 1980 the CIPM approved the report of the CCU (1980) which specified that

            In this definition, it is understood that unbound atoms of carbon 12, at rest and in their ground state, are referred to.
            The definition of the mole also determines the value of the universal constant that relates the number of entities to amount of substance for any sample. This constant is called the Avogadro constant, symbol NA or L. If N(X) denotes the number of entities X in a specified sample, and if n(X) denotes the amount of substance of entities X in the same sample, the relation is

            n(X) = N(X)/NA.
            Note that since N(X) is dimensionless, and n(X) has the SI unit mole, the Avogadro constant has the coherent SI unit reciprocal mole.
        '''
    }

    measurementUnit name: 'candela', symbol: 'cd', {
        id 'http://www.bipm.org/en/publications/si-brochure/candela.html'
        description '''
            The units of luminous intensity based on flame or incandescent filament standards in use in various countries before 1948 were replaced initially by the "new candle" based on the luminance of a Planck radiator (a black body) at the temperature of freezing platinum. This modification had been prepared by the International Commission on Illumination (CIE) and by the CIPM before 1937, and the decision was promulgated by the CIPM in 1946. It was then ratified in 1948 by the 9th CGPM which adopted a new international name for this unit, the candela, symbol cd; in 1967 the 13th CGPM (Resolution 5) gave an amended version of this definition.

            In 1979, because of the difficulties in realizing a Planck radiator at high temperatures, and the new possibilities offered by radiometry, i.e. the measurement of optical radiation power, the 16th CGPM (1979, Resolution 3) adopted a new definition of the candela:

            The candela is the luminous intensity, in a given direction, of a source that emits monochromatic radiation of frequency 540 x 1012 hertz and that has a radiant intensity in that direction of 1/683 watt per steradian.
            It follows that the spectral luminous efficacy for monochromatic radiation of frequency of 540 x 1012 hertz is exactly 683 lumens per watt, K = 683 lm/W = 683 cd sr/W.
            Note that since N(X) is dimensionless, and n(X) has the SI unit mole, the Avogadro constant has the coherent SI unit reciprocal mole.
        '''
    }
    measurementUnit name: "celsius", description: "degrees celsius", symbol: "°C"
    measurementUnit name: "fahrenheit", description: "degrees fahrenheit", symbol: "°F"
    measurementUnit name: "newtons", description: "measurement of force", symbol: "N"
    measurementUnit name: 'area', description: 'square meter', symbol: 'm2'
    measurementUnit name: 'volume', description: 'cubic meter', symbol: 'm3'
    measurementUnit name: 'speed, velocity', description: 'meter per second', symbol: 'm/s'
    measurementUnit name: 'acceleration', description: 'meter per second squared  ', symbol: 'm/s2'
    measurementUnit name: 'wave number', description: 'reciprocal meter', symbol: 'm-1'
    measurementUnit name: 'mass density', description: 'kilogram per cubic meter', symbol: 'kg/m3'
    measurementUnit name: 'specific volume', description: 'cubic meter per kilogram', symbol: 'm3/kg'
    measurementUnit name: 'current density', description: 'ampere per square meter', symbol: 'A/m2'
    measurementUnit name: 'magnetic field strength  ', description: 'ampere per meter', symbol: 'A/m'
    measurementUnit name: 'amount-of-substance concentration', description: 'mole per cubic meter', symbol: 'mol/m3'
    measurementUnit name: 'luminance', description: 'candela per square meter', symbol: 'cd/m2'
    measurementUnit name: 'mass fraction', description: 'kilogram per kilogram', symbol: 'kg/kg = 1'

}



